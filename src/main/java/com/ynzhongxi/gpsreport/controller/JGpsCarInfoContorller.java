package com.ynzhongxi.gpsreport.controller;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ynzhongxi.gpsreport.pojo.HGpsCarDetails;
import com.ynzhongxi.gpsreport.pojo.JCarInfo;
import com.ynzhongxi.gpsreport.pojo.JGpsCarDetails;
import com.ynzhongxi.gpsreport.pojo.JGpsCarInfo;
import com.ynzhongxi.gpsreport.utils.DateFormatUtil;
import com.ynzhongxi.gpsreport.utils.GpsHttpUtil;
import com.ynzhongxi.gpsreport.utils.JxlsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/9/23
 */
@RestController
public class JGpsCarInfoContorller {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private GpsHttpUtil gpsHttpUtil;
    @Scheduled(cron = "0 55 23 ? * *")    //每晚23.55自动执行
    @GetMapping("/insertT")
    public void insertMogoJGpsCarInfo() {
        List<JCarInfo> carInfos = this.mongoTemplate.findAll(JCarInfo.class);   //查询出所有驾驶员数据
        Iterator<JCarInfo> iter = carInfos.iterator();
        List<JGpsCarInfo> jgpsCarInfos = new ArrayList<>();    //保存所有驾驶员的台账信息
        List<JGpsCarDetails>  jGpsCarDetailss=new ArrayList<>();
        while (iter.hasNext()) {
            JCarInfo carInfo = iter.next();
            JGpsCarInfo jgpsCarInfo = new JGpsCarInfo();
            jgpsCarInfo.setCarNumber(carInfo.getCarNumber());   //车牌号
            jgpsCarInfo.setDriverName(carInfo.getDriverName());  //驾驶员名字
            jgpsCarInfo.setPhone(carInfo.getPhone());            //驾驶员电话
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
                    jgpsCarInfo.setPos(info.getStr("pos"));          //地理位置
                    if (new Date().getTime() - 85800000L <= info.getLong("tm") && info.getLong("tm") <= new Date().getTime()) {     //每晚23:50统计最后在线时间,当天时间范围00:00~23:50
                        jgpsCarInfo.setTime(DateFormatUtil.simpleDate(info.getLong("tm")));   //当天最后在线时间
                        jgpsCarInfo.setOnline("是");    //GPS在线
                    } else {
                        jgpsCarInfo.setOnline("否");   //GPS不在线
                    }
                }
            }
            if ("是".equals(jgpsCarInfo.getOnline()) && null == jgpsCarInfo.getPos()) {
                jgpsCarInfo.setType("✔");    //GPS在线，但是返回时间为空，则车台故障
            }
            map.put("begintime", DateFormatUtil.simpleDate(new Date().getTime() - 85800000L));  //开始时间
            map.put("endtime", DateFormatUtil.simpleDate(new Date().getTime()));       //结束时间
            map.put("armType", "11,61,178,180,200,222,223,224,225,226,227,228,229,230,304,309,311,314,99,125,249,299,306,49,99,125,249,299,306,618,619"); //报警类型:  疲劳：49,99,125,249,299,306,618,619
            map.put("pageRecords", 100);   //显示前100条记录                                                      //超速：11,61,178,180,200,222,223,224,225,226,227,228,229,230,304,309,311,314
            map.put("toMap", 2);  //地图经纬度转换  2：百度地图解析可以解析出地址
            String baoJin = gpsHttpUtil.get("/StandardApiAction_queryAlarmDetail.action", map); //获取设备超速、疲劳驾驶报警数据
            JSONObject json3 = JSONUtil.parseObj(baoJin);
            String alarmsStr = json3.getStr("alarms");
            jgpsCarInfo.setTired("");     //无疲劳
            jgpsCarInfo.setSpeed("");    //无超速
            JGpsCarDetails  jGpsCarDetails=null;
            if (JSONUtil.isJsonArray(alarmsStr)) {
                jGpsCarDetails=new JGpsCarDetails();//  每日处理详细表
                JSONArray alarms = JSONUtil.parseArray(alarmsStr);
                for (int i = 0; i < alarms.size(); i++) {
                    int atp = alarms.getJSONObject(i).getInt("atp");   //报警类型
                    if (CollUtil.toList(11, 61, 178, 180, 200, 222, 223, 224, 225, 226, 227, 228, 229, 230, 304, 309, 311, 314).contains(atp)) {
                        jgpsCarInfo.setSpeed("✔");   //超速
                        jgpsCarInfo.setProcessMode("☑已短信告知驾驶员/  □ 已处理   ☑安全教育  □罚款");   //具体处理方式
                        jgpsCarInfo.setData("有");    //有报警数据
                        jGpsCarDetails.setCarNumber(jgpsCarInfo.getCarNumber());
                        jGpsCarDetails.setCarName(jgpsCarInfo.getDriverName());
                        jGpsCarDetails.setType("超速报警");
                        jGpsCarDetails.setTime(alarms.getJSONObject(alarms.size()-1).getStr("bTimeStr"));//报警时间
                        jGpsCarDetails.setSps(alarms.getJSONObject(alarms.size()-1).getStr("sps")); //报警地点
                        jGpsCarDetails.setSpeed(alarms.getJSONObject(alarms.size()-1).getInt("ssp")/10.0);//车速ssp
                        jGpsCarDetails.setWay("短信通知");  //处理方式
                        jGpsCarDetails.setStatus("发送成功"); //回执状态
                        jGpsCarDetails.setWayTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));  //处理时间
                        jGpsCarDetails.setNote("已对其进行批评教育责令改正");//备注
                    }
                    if (CollUtil.toList(49, 99, 125, 249, 299, 306, 618, 619).contains(atp)) {
                        jgpsCarInfo.setTired("✔");   //疲劳
                        jgpsCarInfo.setProcessMode("☑已短信告知驾驶员/  □ 已处理   ☑安全教育  □罚款");   //具体处理方式
                        jgpsCarInfo.setData("有");    //有报警数据
                        jGpsCarDetails.setCarNumber(jgpsCarInfo.getCarNumber());
                        jGpsCarDetails.setCarName(jgpsCarInfo.getDriverName());
                        jGpsCarDetails.setType("疲劳驾驶");
                        jGpsCarDetails.setTime(alarms.getJSONObject(alarms.size()-1).getStr("bTimeStr"));//报警时间
                        jGpsCarDetails.setSps(alarms.getJSONObject(alarms.size()-1).getStr("sps")); //报警地点
                        jGpsCarDetails.setSpeed(alarms.getJSONObject(alarms.size()-1).getInt("ssp")/10.0);//车速ssp
                        jGpsCarDetails.setWay("短信通知");  //处理方式
                        jGpsCarDetails.setStatus("发送成功"); //回执状态
                        jGpsCarDetails.setWayTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));  //处理时间
                        jGpsCarDetails.setNote("已对其进行批评教育责令改正");//备注
                    }
                }
            } else {
                jgpsCarInfo.setData("无");    //无报警数据
                jgpsCarInfo.setProcessMode("□已短信告知驾驶员/  □ 已处理   □安全教育  □罚款");
            }

            String sps = gpsHttpUtil.get("/StandardApiAction_getDeviceStatus.action", map);   //获取车辆速度
            JSONObject json4 = JSONUtil.parseObj(sps);
            String statusStr = json4.getStr("status");
            if (JSONUtil.isJsonArray(statusStr)) {
                JSONObject status = JSONUtil.parseArray(statusStr).getJSONObject(0);
                String sp = status.getStr("sp");
                if (sp != null) {     //速度内容不能为空
                    try {
                        if (new Date().getTime() - 85800000L <=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(status.getStr("gt")).getTime() && new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(status.getStr("gt")).getTime() <= new Date().getTime()){
                            jgpsCarInfo.setSp(Integer.parseInt(sp) / 10.0);  //当天的速度
                        }
                        else {
                            jgpsCarInfo.setSp(0.0);            //不再当天速度为0
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            jgpsCarInfos.add(jgpsCarInfo);   //添加一条司机信息保存到ArrayList集合中
            jGpsCarDetailss.add(jGpsCarDetails);//添加一条每日详细
        }
        this.mongoTemplate.insertAll(jgpsCarInfos);   //将所有回通司机的gps信息插入到数据库
        this.mongoTemplate.insertAll(jGpsCarDetailss);// 将所有每日详细保存到数据库
    }
    @GetMapping("/getByTimeJGpsCarInfo")
    public void getByTimeJGpsCarInfo(){
        List<JGpsCarInfo> jGpsCarInfos = this.mongoTemplate.findAll( JGpsCarInfo.class);
        long count= this.mongoTemplate.count(new Query(new Criteria().orOperator(Criteria.where("_class").exists(true))),JGpsCarInfo.class);  //安装GPS的的数量
        long count1 = this.mongoTemplate.count(new Query(Criteria.where("online").is("是")), JGpsCarInfo.class);//GPS在线数量
        Iterator<JGpsCarInfo> iter= jGpsCarInfos.iterator();
        int x=1;
        while(iter.hasNext()){
            JGpsCarInfo jGpsCarInfo = iter.next();
            jGpsCarInfo.setNum(x++);
            if("否".equals(jGpsCarInfo.getOnline())){
                jGpsCarInfo.setPos(" ");
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("gpsCarInfos", jGpsCarInfos);
        map.put("newDate", new SimpleDateFormat("yyyy年MM月dd日").format(new Date()));  //当天时间
        map.put("total", count);   //GPS安装总台数
        map.put("online", count1);  //GPS在线台数
        map.put("noonlone", this.mongoTemplate.count(new Query(Criteria.where("online").is("否")),JGpsCarInfo.class));    //GPS不在线台数
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后1位
        numberFormat.setMaximumFractionDigits(1);
        String result = numberFormat.format((float) count1 / (float) count * 100) + "%";   //在线率计算
        map.put("OnlineRate", result);
        // 模板路径和输出流
        String templatePath = "E:\\jxls\\运营车辆GPS监控平台监控管理台账.xls";
        String excelName = "E:\\jxls\\" + new SimpleDateFormat("yyyy年MM月dd日").format(new Date()) + "锦通运营车辆GPS监控平台监控管理锦通台账.xls";
        try{
            OutputStream os = new FileOutputStream(excelName);
            //调用封装的工具类，传入模板路径，输出流，和装有数据的Map,按照模板导出
            JxlsUtil.exportExcel(templatePath, os, map);
            os.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @GetMapping("/getJGpsCarDetail")
    public void getJGpsCarDetail(){
        String time = "2019-09-23";
        Criteria criteria=Criteria.where("wayTime").regex(".*?"+time+".*");
        Query query = new Query(criteria);
        List<JGpsCarDetails> jGpsCarDetails = this.mongoTemplate.find(query, JGpsCarDetails.class);
        Iterator<JGpsCarDetails>  iter=jGpsCarDetails.iterator();
        int n=1;
        while(iter.hasNext()){
            JGpsCarDetails   jGpsCarDetails1=iter.next();
            jGpsCarDetails1.setNum(n++);
        }
        Map<String ,Object>  map=new HashMap<>();
        map.put("gpsCarDetails",jGpsCarDetails);
        map.put("count",jGpsCarDetails.size());
        map.put("iscount",jGpsCarDetails.size());
        map.put("OnlineRate", "100%");
        map.put("newDate", new SimpleDateFormat("yyyy年MM月dd日").format(new Date()));  //当天时间
        // 模板路径和输出流
        String templatePath = "E:\\jxls\\报警处理明细.xlsx";
        String excelName = "E:\\jxls\\" + new SimpleDateFormat("yyyy年MM月dd日").format(new Date()) + "锦通报警处理明细锦通.xls";
        try{
        OutputStream os = new FileOutputStream(excelName);
        //调用封装的工具类，传入模板路径，输出流，和装有数据的Map,按照模板导出
        JxlsUtil.exportExcel(templatePath, os, map);
        os.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

