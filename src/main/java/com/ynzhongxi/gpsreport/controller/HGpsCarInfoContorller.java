package com.ynzhongxi.gpsreport.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ynzhongxi.gpsreport.pojo.*;
import com.ynzhongxi.gpsreport.service.HGpsCarInfoService;
import com.ynzhongxi.gpsreport.utils.DateFormatUtil;
import com.ynzhongxi.gpsreport.utils.GpsHttpUtil;
import com.ynzhongxi.gpsreport.utils.JxlsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.RequestWrapper;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/9/11
 */
@RestController
@RequestMapping(value = "/hGpsCarInfo")
public class HGpsCarInfoContorller extends BaseController {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private GpsHttpUtil gpsHttpUtil;
    @Autowired
    private HGpsCarInfoService hGpsCarInfoService;

    @Scheduled(cron = "0 50 23 ? * *")    //每晚23:50自动执行
    @GetMapping("/insertH")
    public void insertMogoHGpsCarInfo() {
        List<HCarInfo> carInfos = this.mongoTemplate.find(new Query(Criteria.where("deviceId").exists(true)), HCarInfo.class);   //查询出设备号不为空的所有驾驶员数据
        Iterator<HCarInfo> iter = carInfos.iterator();
        List<HGpsCarInfo> hgpsCarInfos = new ArrayList<>();    //保存所有驾驶员的台账信息
        List<HGpsCarDetails> hgpsCarDetails = new ArrayList<>();    //全部每日处理详细表
        while (iter.hasNext()) {
            HCarInfo carInfo = iter.next();
            HGpsCarInfo hgpsCarInfo = new HGpsCarInfo();
            hgpsCarInfo.setCarNumber(carInfo.getCarNumber());   //车牌号
            hgpsCarInfo.setDriverName(carInfo.getDriverName());  //驾驶员名字
            hgpsCarInfo.setPhone(carInfo.getPhone());            //驾驶员电话
            Map<String, Object> map = new HashMap<>();
            map.put("devIdno", carInfo.getDeviceId());   //设备号
            map.put("vehiIdno", carInfo.getCarNumber());  //车牌号
            map.put("geoaddress", 1);//解析最新地理位置
            String in = gpsHttpUtil.get("/StandardApiAction_vehicleStatus.action", map);//获取车辆最新位置
            JSONObject json2 = JSONUtil.parseObj(in);
            String infoStr = json2.getStr("infos");
            if (JSONUtil.isJsonArray(infoStr)) {
                JSONObject info = JSONUtil.parseArray(infoStr).getJSONObject(0);
                if (info.getStr("pos") != "" && info.getStr("tm") != null) {   //判断返回的地理位置不能为空、最后在线时间不能为null
                    hgpsCarInfo.setPos(info.getStr("pos"));          //地理位置
                    if (new Date().getTime() - 85800000L <= info.getLong("tm") && info.getLong("tm") <= new Date().getTime()) {     //每晚23:50统计最后在线时间,当天时间范围00:00~23:50
                        hgpsCarInfo.setTime(DateFormatUtil.simpleDate(info.getLong("tm")));   //当天最后在线时间
                        hgpsCarInfo.setOnline("是");    //GPS在线
                    } else {
                        hgpsCarInfo.setOnline("否");   //GPS不在线
                    }
                }
            }
            if ("是".equals(hgpsCarInfo.getOnline()) && null == hgpsCarInfo.getPos()) {
                hgpsCarInfo.setType("✔");    //GPS在线，但是返回地址为空，则车台故障
            }
            map.put("begintime", DateFormatUtil.simpleDate(new Date().getTime() - 85800000L));  //开始时间
            map.put("endtime", DateFormatUtil.simpleDate(new Date().getTime()));       //结束时间
            map.put("armType", "11,61,178,180,200,222,223,224,225,226,227,228,229,230,304,309,311,314,99,125,249,299,306,49,99,125,249,299,306,618,619"); //报警类型:  疲劳：49,99,125,249,299,306,618,619
            map.put("pageRecords", 100);   //显示前100条记录                                                      //超速：11,61,178,180,200,222,223,224,225,226,227,228,229,230,304,309,311,314
            map.put("toMap", 2);  //地图经纬度转换  2：百度地图解析可以解析出地址
            String baoJin = gpsHttpUtil.get("/StandardApiAction_queryAlarmDetail.action", map); //获取设备超速、疲劳驾驶报警数据
            JSONObject json3 = JSONUtil.parseObj(baoJin);
            String alarmsStr = json3.getStr("alarms");
            hgpsCarInfo.setTired("");     //无疲劳
            hgpsCarInfo.setSpeed("");    //无超速
            HGpsCarDetails hgGpsCarDetails = null;
            if (JSONUtil.isJsonArray(alarmsStr)) {
                hgGpsCarDetails = new HGpsCarDetails();//  每日处理详细表
                JSONArray alarms = JSONUtil.parseArray(alarmsStr);
                for (int i = 0; i < alarms.size(); i++) {
                    int atp = alarms.getJSONObject(i).getInt("atp");   //报警类型
                    if (CollUtil.toList(11, 61, 178, 180, 200, 222, 223, 224, 225, 226, 227, 228, 229, 230, 304, 309, 311, 314).contains(atp)) {
                        hgpsCarInfo.setSpeed("✔");   //超速
                        hgpsCarInfo.setProcessMode("☑已短信告知驾驶员/  □ 已处理   ☑安全教育  □罚款");   //具体处理方式
                        hgpsCarInfo.setData("有");    //有报警数据
                        hgGpsCarDetails.setCarNumber(hgpsCarInfo.getCarNumber());
                        hgGpsCarDetails.setCarName(hgpsCarInfo.getDriverName());
                        hgGpsCarDetails.setType("超速报警");
                        hgGpsCarDetails.setTime(alarms.getJSONObject(alarms.size() - 1).getStr("bTimeStr"));//报警时间
                        hgGpsCarDetails.setSps(alarms.getJSONObject(alarms.size() - 1).getStr("sps")); //报警地点
                        hgGpsCarDetails.setSpeed(alarms.getJSONObject(alarms.size() - 1).getInt("ssp") / 10.0);//车速ssp
                        hgGpsCarDetails.setWay("短信通知");  //处理方式
                        hgGpsCarDetails.setStatus("发送成功"); //回执状态
                        hgGpsCarDetails.setWayTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));  //处理时间
                        hgGpsCarDetails.setNote("已对其进行批评教育责令改正");//备注
                    }
                    if (CollUtil.toList(49, 99, 125, 249, 299, 306, 618, 619).contains(atp)) {
                        hgpsCarInfo.setTired("✔");   //疲劳
                        hgpsCarInfo.setProcessMode("☑已短信告知驾驶员/  □ 已处理   ☑安全教育  □罚款");   //具体处理方式
                        hgpsCarInfo.setData("有");    //有报警数据
                        hgGpsCarDetails.setCarNumber(hgpsCarInfo.getCarNumber());
                        hgGpsCarDetails.setCarName(hgpsCarInfo.getDriverName());
                        hgGpsCarDetails.setType("疲劳驾驶");
                        hgGpsCarDetails.setTime(alarms.getJSONObject(alarms.size() - 1).getStr("bTimeStr"));//报警时间
                        hgGpsCarDetails.setSps(alarms.getJSONObject(alarms.size() - 1).getStr("sps")); //报警地点
                        hgGpsCarDetails.setSpeed(alarms.getJSONObject(alarms.size() - 1).getInt("ssp") / 10.0);//车速ssp
                        hgGpsCarDetails.setWay("短信通知");  //处理方式
                        hgGpsCarDetails.setStatus("发送成功"); //回执状态
                        hgGpsCarDetails.setWayTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));  //处理时间
                        hgGpsCarDetails.setNote("已对其进行批评教育责令改正");//备注
                    }
                }
            } else {
                hgpsCarInfo.setData("无");    //无报警数据
                hgpsCarInfo.setProcessMode("□已短信告知驾驶员/  □ 已处理   □安全教育  □罚款");
            }

            String sps = gpsHttpUtil.get("/StandardApiAction_getDeviceStatus.action", map);   //获取车辆速度
            JSONObject json4 = JSONUtil.parseObj(sps);
            String statusStr = json4.getStr("status");
            if (JSONUtil.isJsonArray(statusStr)) {
                JSONObject status = JSONUtil.parseArray(statusStr).getJSONObject(0);
                String sp = status.getStr("sp");
                if (sp != null) {     //速度内容不能为空
                    try {
                        if (new Date().getTime() - 85800000L <= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(status.getStr("gt")).getTime() && new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(status.getStr("gt")).getTime() <= new Date().getTime()) {
                            hgpsCarInfo.setSp(Integer.parseInt(sp) / 10.0);  //当天的速度
                        } else {
                            hgpsCarInfo.setSp(0.0);            //不再当天速度为0
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } else {
                hgpsCarInfo.setSp(0.0);            //没有数据速度为0
            }
            hgpsCarInfo.setMonth(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));   //数据月份
            hgpsCarInfos.add(hgpsCarInfo);   //添加一条司机信息到ArrayList集合中
            hgpsCarDetails.add(hgGpsCarDetails);  //添加一条每日详细
        }
        this.mongoTemplate.insertAll(hgpsCarInfos);   //将所有回通司机的gps信息插入到数据库
        this.mongoTemplate.insertAll(hgpsCarDetails);//将每日详细插入到数据库
    }

    @GetMapping("/getByTimeHGpsCarInfo")
    public void getByTimeHGpsCarInfo(String time) throws Exception {
        Criteria criteria = Criteria.where("month").regex(".*?" + time + ".*");
        Query query = new Query(criteria);
        List<HGpsCarInfo> hGpsCarInfos = this.mongoTemplate.find(query, HGpsCarInfo.class);
        long count = this.mongoTemplate.count(query, HGpsCarInfo.class);  //安装GPS的的数量
        long count1 = this.mongoTemplate.count(new Query(Criteria.where("online").is("是").and("month").is(time)), HGpsCarInfo.class);//GPS在线数量
        Iterator<HGpsCarInfo> iter = hGpsCarInfos.iterator();
        int x = 1;
        while (iter.hasNext()) {
            HGpsCarInfo hGpsCarInfo = iter.next();
            hGpsCarInfo.setNum(x++);     //序号
            if ("否".equals(hGpsCarInfo.getOnline())) {
                hGpsCarInfo.setPos(" ");
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("gpsCarInfos", hGpsCarInfos);
        map.put("newDate", time);  //当天时间
        map.put("total", count);   //GPS安装总台数
        map.put("online", count1);  //GPS在线台数
        map.put("noonlone", count - count1);    //GPS不在线台数
        map.put("name", "回通");
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后1位
        numberFormat.setMaximumFractionDigits(1);
        String result = numberFormat.format((float) count1 / (float) count * 100) + "%";   //在线率计算
        map.put("OnlineRate", result);
        // 模板路径和输出流
        String templatePath = "E:\\jxls\\运营车辆GPS监控平台监控管理台账.xls";
        String excelName = "E:\\jxls\\" + time + "回通运营车辆GPS监控平台监控管理回通台账.xls";

        OutputStream os = new FileOutputStream(excelName);
        //调用封装的工具类，传入模板路径，输出流，和装有数据的Map,按照模板导出
        JxlsUtil.exportExcel(templatePath, os, map);
        os.close();
    }

    @GetMapping("/getHGpsCarInfoByTimeList")
    public Page<HGpsCarInfo> getListHGpsCarInfo(HGpsCarInfo hGpsCarInfo,
                                                @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                @RequestParam(name = "limit", required = false, defaultValue = "10") int pageSize) {
        return this.hGpsCarInfoService.getListHGpsCarInfo(hGpsCarInfo, page, pageSize);
    }

    @GetMapping("/getHMonthCountExport")
    public void getHMonthCountExport(String month) throws Exception {
        Map<String, Object> map = this.hGpsCarInfoService.getHMonthCount(month);
        // 模板路径和输出流
        String templatePath = "E:\\jxls\\月季度运营车辆GPS监控平台监控管理台账.xlsx";
        String excelName = "E:\\jxls\\" + month + "回通月季度运营车辆GPS监控平台监控管理台账.xls";

        OutputStream os = new FileOutputStream(excelName);
        //调用封装的工具类，传入模板路径，输出流，和装有数据的Map,按照模板导出
        JxlsUtil.exportExcel(templatePath, os, map);
        os.close();
    }

    @GetMapping("/getHMonthCount")
    public Map<String, Object> getHMonthCount(@RequestParam(name = "month", required = false, defaultValue = "") String month) throws Exception {
        Map<String, Object> map = this.hGpsCarInfoService.getHMonthCount(month);
        return map;
    }

    @GetMapping("/getHGpsCarDetailByTime")
    public Page<HGpsCarDetails> getHGpsCarDetailByTime(@RequestParam(name = "time", required = false, defaultValue = "") String time,
                                                       @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                       @RequestParam(name = "limit", required = false, defaultValue = "10") int pageSize) {
        return this.hGpsCarInfoService.getHGpsCarDetailByTime(time, page, pageSize);
    }

    @GetMapping("/getHGpsCarDetail")
    public Page<HGpsCarDetails> getHGpsCarDetail(@RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                 @RequestParam(name = "limit", required = false, defaultValue = "10") int pageSize) {
        return this.hGpsCarInfoService.getHGpsCarDetail(page, pageSize);
    }

    @GetMapping("/exportHGpsCarDetail")
    public void exportHGpsCarDetail(String time) throws Exception {
        Criteria criteria = Criteria.where("time").regex(".*?" + time + ".*");
        Query query = new Query(criteria);
        List<HGpsCarDetails> hGpsCarDetails = this.mongoTemplate.find(query, HGpsCarDetails.class);
        Iterator<HGpsCarDetails> iter = hGpsCarDetails.iterator();
        int n = 1;
        while (iter.hasNext()) {
            HGpsCarDetails hGpsCarDetails1 = iter.next();
            hGpsCarDetails1.setNum(n++);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("gpsCarDetails", hGpsCarDetails);
        map.put("count", hGpsCarDetails.size());
        map.put("iscount", hGpsCarDetails.size());
        map.put("OnlineRate", "100%");
        map.put("newDate", time);  //当天时间
        map.put("name", "回通");
        // 模板路径和输出流
        String templatePath = "E:\\jxls\\报警处理明细.xlsx";
        String excelName = "E:\\jxls\\" +time + "回通报警处理明细回通.xls";
        OutputStream os = new FileOutputStream(excelName);
        //调用封装的工具类，传入模板路径，输出流，和装有数据的Map,按照模板导出
        JxlsUtil.exportExcel(templatePath, os, map);
        os.close();
    }
}
