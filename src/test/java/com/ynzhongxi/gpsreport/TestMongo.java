package com.ynzhongxi.gpsreport;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.ynzhongxi.gpsreport.component.RedisUtils;
import com.ynzhongxi.gpsreport.pojo.*;
import com.ynzhongxi.gpsreport.service.HGpsCarInfoService;
import com.ynzhongxi.gpsreport.utils.DateFormatUtil;
import com.ynzhongxi.gpsreport.utils.GpsHttpUtil;
import com.ynzhongxi.gpsreport.utils.JxlsUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.Principal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/9/4
 */
@SpringBootTest(classes = GpsreportApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class TestMongo extends BaseSpringBootTest {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static String LOGURL = "http://60.161.53.204:8088/StandardApiAction_login.action?account=htgs&password=000000";
    @Autowired
    private GpsHttpUtil gpsHttpUtil;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private HGpsCarInfoService hGpsCarInfoService;
    @Resource
    RedisUtils redis;

    @Override
    public String baseUrl() {
        return null;
    }

    @Test
    public void testRerdis() {
        //String  res= HttpUtil.post(LOGURL,"");
        // LogJsession logsession= JSONUtil.toBean(res, LogJsession.class);
        //System.out.println(redis.get("jsession"));
        System.out.println(redisTemplate.opsForValue().get("jsession"));
    }

    @Test
    public void testGetCarInfo() {
        String path = "http://60.161.53.204:8088/StandardApiAction_queryUserVehicle.action?jsession=298d6901-ce04-4ee9-8082-fb3ac3eb8d77";
        String res = HttpUtil.post(path, "");
        JSONObject json = JSONUtil.parseObj(res);
        JSONArray vehicles = json.getJSONArray("vehicles");
        for (int i = 0; i < vehicles.size(); i++) {
            JSONObject obj = vehicles.getJSONObject(i);
            String nm = obj.getStr("nm");
            String id = obj.getJSONArray("dl").getJSONObject(0).getStr("id");
            System.out.println(nm);
            System.out.println(id);
        }
    }

    @Test
    public void get() {
        Map<String, Object> map = new HashMap<>();
        map.put("begintime", "2019-9-05 00:00:00");  //开始时间
        map.put("endtime", "2019-9-05 23:59:59");       //结束时间
        map.put("armType", "14");    //报警类型
        map.put("pageRecords", 100);   //显示前100条记录
        map.put("devIdno", "014192908405");
        System.out.println(gpsHttpUtil.get("/StandardApiAction_queryAlarmDetail.action", map));
    }

    @Test
    public void insertHGpsCarInfo() {
        this.mongoTemplate.dropCollection("hGpsCarInfo");
        List<HCarInfo> carInfos = this.mongoTemplate.findAll(HCarInfo.class);   //查询出所有驾驶员数据
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
                hgpsCarInfo.setType("✔");    //GPS在线，但是返回时间为空，则车台故障
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
                        hgpsCarInfo.setProcessMode("□已短信告知驾驶员/  ☑ 已处理   □安全教育  □罚款");   //具体处理方式
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
                        hgpsCarInfo.setProcessMode("□已短信告知驾驶员/  ☑ 已处理   □安全教育  □罚款");   //具体处理方式
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
                    hgpsCarInfo.setSp(Integer.parseInt(sp) / 10.0);
                }
            }
            hgpsCarInfos.add(hgpsCarInfo);   //添加一条司机信息到ArrayList集合中
            hgpsCarDetails.add(hgGpsCarDetails);  //添加一条每日详细
        }
        this.mongoTemplate.insertAll(hgpsCarInfos);   //将所有回通司机的gps信息插入到数据库
        this.mongoTemplate.insertAll(hgpsCarDetails);//将每日详细插入到数据库
    }

    @Test
    public void getHGpsCarInfo() throws Exception {
        String time = "2019-09-23";
        Criteria criteria = Criteria.where("time").regex(".*?" + time + ".*");
        Query query = new Query(criteria);
        List<HGpsCarInfo> hGpsCarInfos = this.mongoTemplate.find(query, HGpsCarInfo.class);
        long count = this.mongoTemplate.count(new Query(new Criteria().orOperator(Criteria.where("_class").exists(true))), HCarInfo.class);  //安装GPS的的数量
        long count1 = this.mongoTemplate.count(new Query(Criteria.where("online").is("是")), HGpsCarInfo.class);//GPS在线数量
        Iterator<HGpsCarInfo> iter = hGpsCarInfos.iterator();
        int x = 1;
        while (iter.hasNext()) {
            HGpsCarInfo hGpsCarInfo = iter.next();
            hGpsCarInfo.setNum(x++);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("hgpsCarInfos", hGpsCarInfos);
        map.put("newDate", new SimpleDateFormat("yyyy年MM月dd日").format(new Date()));  //当天时间
        map.put("total", count);   //GPS安装总台数
        map.put("online", count1);  //GPS在线台数
        map.put("noonlone", this.mongoTemplate.count(new Query(Criteria.where("online").is("否")), HGpsCarInfo.class));    //GPS不在线台数
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后1位
        numberFormat.setMaximumFractionDigits(1);
        String result = numberFormat.format((float) count1 / (float) count * 100) + "%";   //在线率计算
        map.put("OnlineRate", result);

        // 模板路径和输出流
        String templatePath = "E:\\jxls\\官厅运营车辆GPS监控平台监控管理台账.xls";
        String excelName = "E:\\jxls\\" + new SimpleDateFormat("yyyy年MM月dd日").format(new Date()) + "官厅运营车辆GPS监控平台监控管理回通台账.xls";
        OutputStream os = new FileOutputStream(excelName);
        //调用封装的工具类，传入模板路径，输出流，和装有数据的Map,按照模板导出
        JxlsUtil.exportExcel(templatePath, os, map);
        os.close();
    }

    @Test
    public void insertJGpsCarInfo() {
        this.mongoTemplate.dropCollection("jGpsCarInfo");
        List<JCarInfo> carInfos = this.mongoTemplate.findAll(JCarInfo.class);   //查询出所有驾驶员数据
        Iterator<JCarInfo> iter = carInfos.iterator();
        List<JGpsCarInfo> jgpsCarInfos = new ArrayList<>();    //保存所有驾驶员的台账信息
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
            if (JSONUtil.isJsonArray(alarmsStr)) {
                JSONArray alarms = JSONUtil.parseArray(alarmsStr);
                for (int i = 0; i < alarms.size(); i++) {
                    int atp = alarms.getJSONObject(i).getInt("atp");   //报警类型
                    if (CollUtil.toList(11, 61, 178, 180, 200, 222, 223, 224, 225, 226, 227, 228, 229, 230, 304, 309, 311, 314).contains(atp)) {
                        jgpsCarInfo.setSpeed("✔");   //超速
                        jgpsCarInfo.setProcessMode("□已短信告知驾驶员/  ☑ 已处理   □安全教育  □罚款");   //具体处理方式
                        jgpsCarInfo.setData("有");    //有报警数据
                    }
                    if (CollUtil.toList(49, 99, 125, 249, 299, 306, 618, 619).contains(atp)) {
                        jgpsCarInfo.setTired("✔");   //疲劳
                        jgpsCarInfo.setProcessMode("□已短信告知驾驶员/  ☑ 已处理   □安全教育  □罚款");   //具体处理方式
                        jgpsCarInfo.setData("有");    //有报警数据
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
                    jgpsCarInfo.setSp(Integer.parseInt(sp) / 10.0);
                }
            }
            jgpsCarInfos.add(jgpsCarInfo);   //添加一条司机信息到ArrayList集合中
        }
        this.mongoTemplate.insertAll(jgpsCarInfos);   //将所有回通司机的gps信息插入到数据库
    }

    @Test
    public void getJGpsCarInfo() throws Exception {
        String time = "2019-09-23";
        Criteria criteria = Criteria.where("time").regex(".*?" + time + ".*");
        Query query = new Query(criteria);
        List<JGpsCarInfo> jGpsCarInfos = this.mongoTemplate.find(query, JGpsCarInfo.class);
        long count = this.mongoTemplate.count(new Query(new Criteria().orOperator(Criteria.where("_class").exists(true))), JGpsCarInfo.class);  //安装GPS的的数量
        long count1 = this.mongoTemplate.count(new Query(Criteria.where("online").is("是")), JGpsCarInfo.class);//GPS在线数量
        Iterator<JGpsCarInfo> iter = jGpsCarInfos.iterator();
        int x = 1;
        while (iter.hasNext()) {
            JGpsCarInfo jGpsCarInfo = iter.next();
            jGpsCarInfo.setNum(x++);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("hgpsCarInfos", jGpsCarInfos);
        map.put("newDate", new SimpleDateFormat("yyyy年MM月dd日").format(new Date()));  //当天时间
        map.put("total", count);   //GPS安装总台数
        map.put("online", count1);  //GPS在线台数
        map.put("noonlone", this.mongoTemplate.count(new Query(Criteria.where("online").is("否")), JGpsCarInfo.class));    //GPS不在线台数
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后1位
        numberFormat.setMaximumFractionDigits(1);
        String result = numberFormat.format((float) count1 / (float) count * 100) + "%";   //在线率计算
        map.put("OnlineRate", result);

        // 模板路径和输出流
        String templatePath = "E:\\jxls\\官厅运营车辆GPS监控平台监控管理台账.xls";
        String excelName = "E:\\jxls\\" + new SimpleDateFormat("yyyy年MM月dd日").format(new Date()) + "官厅运营车辆GPS监控平台监控管理锦通台账.xls";
        OutputStream os = new FileOutputStream(excelName);
        //调用封装的工具类，传入模板路径，输出流，和装有数据的Map,按照模板导出
        JxlsUtil.exportExcel(templatePath, os, map);
        os.close();
    }

    @Test
    public void getHGpsCarDetail() throws Exception {
        String time = "2019-09-23";
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
        map.put("hGpsCarDetails", hGpsCarDetails);
        map.put("count", hGpsCarDetails.size());
        map.put("iscount", hGpsCarDetails.size());
        map.put("OnlineRate", "100%");
        map.put("newDate", new SimpleDateFormat("yyyy年MM月dd日").format(new Date()));  //当天时间
        // 模板路径和输出流
        String templatePath = "E:\\jxls\\官厅报警处理明细回通.xlsx";
        String excelName = "E:\\jxls\\" + new SimpleDateFormat("yyyy年MM月dd日").format(new Date()) + "官厅报警处理明细回通.xls";
        OutputStream os = new FileOutputStream(excelName);
        //调用封装的工具类，传入模板路径，输出流，和装有数据的Map,按照模板导出
        JxlsUtil.exportExcel(templatePath, os, map);
        os.close();
    }

    @Test
    public void getCarInfo() throws Exception {
        List<HGpsCarInfo> gpsCarInfos = this.mongoTemplate.findAll(HGpsCarInfo.class);//车台故障的所有数据
        Iterator<HGpsCarInfo> iterator = gpsCarInfos.iterator();
        List<HGpsCarInfo> gpsCarInfos1 = new ArrayList<>();
        int i = 0;
        while (iterator.hasNext()) {
            HGpsCarInfo gpsCarInfo = iterator.next();
            gpsCarInfo.setNum(++i);
            gpsCarInfos1.add(gpsCarInfo);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("gpsCarInfos", gpsCarInfos1);
        map.put("newDate", new SimpleDateFormat("yyyy年MM月dd日").format(new Date()));  //当天时间
        // 模板路径和输出流
        String templatePath = "E:\\jxls\\官厅运营车辆GPS监控平台监控管理台账.xls";
        String excelName = "E:\\jxls\\" + new SimpleDateFormat("yyyy年MM月dd日").format(new Date()) + "官厅运营车辆GPS监控平台监控管理台账.xls";
        OutputStream os = new FileOutputStream(excelName);
        //调用封装的工具类，传入模板路径，输出流，和装有数据的Map,按照模板导出
        JxlsUtil.exportExcel(templatePath, os, map);
        os.close();

    }

    @Test
    public void ExcelHT() throws Exception {
        ExcelReader reader = ExcelUtil.getReader("C:\\Users\\gt\\Desktop\\车辆GPS在线排查表.xlsx");
        List<Map<String, Object>> readAll = reader.readAll();
        List<CarInfo> all = mongoTemplate.findAll(CarInfo.class);
        List<HCarInfo> jCarInfos = new ArrayList<>();
        for (int i = 0; i < readAll.size(); i++) {
            HCarInfo jCarInfo = new HCarInfo();
            Map<String, Object> map = readAll.get(i);
            String carNumber = (String) map.get("车牌");
            int z = 0;
            for (int x = 0; x < all.size(); x++) {
                if (all.get(x).getCarNumber().equals(carNumber)) {
                    jCarInfo.setDriverName(all.get(x).getDriverName());
                    jCarInfo.setPhone(all.get(x).getPhone());
                    jCarInfo.setCarNumber(all.get(x).getCarNumber());
                    jCarInfo.setDeviceId(all.get(x).getDeviceId());
                    jCarInfos.add(jCarInfo);
                    z = 1;
                }
            }
            if (z == 0) {
                System.err.println(carNumber);
            }

        }
        System.out.println(jCarInfos.size());
        this.mongoTemplate.insertAll(jCarInfos);
    }

    @Test
    public void ExcelJT() throws Exception {
        ExcelReader reader = ExcelUtil.getReader("C:\\Users\\gt\\Desktop\\锦通.xlsx");
        List<Map<String, Object>> readAll = reader.readAll();
        List<CarInfo> all = mongoTemplate.findAll(CarInfo.class);
        for (int i = 0; i < readAll.size(); i++) {
            Map<String, Object> map = readAll.get(i);
            String carNumber = (String) map.get("车牌");
            boolean flag = true;
            for (int x = 0; x < all.size(); x++) {
                if (all.get(x).getCarNumber().equals(carNumber)) {   //车牌号相同的
                    if (null == all.get(x).getDriverName()) {   //没有姓名的驾驶员
                        System.err.println(all.get(x));
                    }
                    flag = false;
                }
            }
            if (flag) {      //没有驾驶员车牌号的
                System.err.println(carNumber);
            }
        }
    }

    @Test
    public void testJT() throws Exception {
        Criteria criteria = new Criteria();
        criteria.and("carNumber").is("云G76787");
        Query query = new Query(criteria);
        List<CarInfo> carInfos = mongoTemplate.find(query, CarInfo.class);
        System.out.println(carInfos);
    }

    @Test
    public void findHcarInfo() {
        List<HCarInfo> all = this.mongoTemplate.findAll(HCarInfo.class);
    }

    @Test
    public void findJcarInfo() {
        List<JCarInfo> all = this.mongoTemplate.findAll(JCarInfo.class);
    }

    @Test
    public void editHcarInfo() {
        HCarInfo hCarInfo = this.mongoTemplate.findById("5d882f6b97a5823468281a41", HCarInfo.class);
        System.out.println(hCarInfo);
    }

    @Test
    public void inertHcarInfo() {
        ExcelReader reader = ExcelUtil.getReader("C:\\Users\\gt\\Desktop\\锦通82.xls");
        List<Map<String, Object>> readAll = reader.readAll();
        Iterator<Map<String, Object>> iter = readAll.iterator();
        List<JCarInfo> hCarInfos = new ArrayList<JCarInfo>();
        int n = 1;
        while (iter.hasNext()) {
            n++;
            Map<String, Object> next = iter.next();
            String carNumber = (String) next.get("车牌号");
            String name = (String) next.get("联系人");
            String phone = (String) next.get("联系人手机");
            JCarInfo hCarInfo = new JCarInfo();
            hCarInfo.setCarNumber(carNumber);
            hCarInfo.setDriverName(name);
            hCarInfo.setPhone(phone);
            hCarInfos.add(hCarInfo);

//            Map<String ,Object>  map=new HashMap<>();
//            map.put("vehiIdno",carNumber);
//            String  result=gpsHttpUtil.get("/StandardApiAction_getDeviceByVehicle.action",map);
//            JSONObject jsonObj = JSONUtil.parseObj(result);
//            int   i=jsonObj.getInt("result");
//              if(i==8){
//                 // System.err.println(carNumber);
//              }
//              if(i==18){
//                  //System.err.println(n+"      "+carNumber);
//              }
//              if(i==0){
//
//              }

        }
        this.mongoTemplate.insertAll(hCarInfos);
    }

    @Test
    public void inertcarInfo() {
        List<JCarInfo> all = this.mongoTemplate.findAll(JCarInfo.class);
        Iterator<JCarInfo> iterator = all.iterator();
        while (iterator.hasNext()) {
            JCarInfo next = iterator.next();
            String carNumber = next.getCarNumber();
            Criteria criteria = new Criteria();
            criteria.and("carNumber").is(carNumber);
            Query query = new Query(criteria);
            List<CarInfo> carInfos = this.mongoTemplate.find(query, CarInfo.class);
            Update update = new Update();
            System.out.println(carInfos.size() > 0 ? carInfos.get(0).getDeviceId() : " ");
            update.set("deviceId", carInfos.size() > 0 ? carInfos.get(0).getDeviceId() : " ");
            // Criteria criteria1 = new Criteria();
            // criteria.and("carNumber").is(carInfos.size()>0?carInfos.get(0).getCarNumber():" ");
            Query query1 = new Query(Criteria.where("carNumber").is(carInfos.size() > 0 ? carInfos.get(0).getCarNumber() : " "));
            System.out.println("****************" + this.mongoTemplate.updateFirst(query1, update, JCarInfo.class));

        }
    }

    @Test
    public void notNull() {
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("driverName").exists(true));//不为空的所有数据
        //criteria.orOperator(Criteria.where("driverName").exists(false);  //为空的所有数据
        Query query = new Query(criteria);
        List<JCarInfo> gpsCarInfos = mongoTemplate.find(query, JCarInfo.class);
        Iterator<JCarInfo> iter = gpsCarInfos.iterator();
        while (iter.hasNext()) {
            JCarInfo next = iter.next();
            System.out.println(next.getCarNumber());
        }

    }

    @Test
    public void findByCarNumber() {
        Criteria criteria = new Criteria();
        criteria.and("carNumber").is("云G71427");
        Query query = new Query(criteria);
        System.out.println(this.mongoTemplate.find(query, CarInfo.class));
    }

    @Test
    public void getFiledName() {
        String month = "2019-09";
        Map<String, Object> map = new HashMap<>();
        Criteria tcriteria = new Criteria();
        tcriteria.and("time").regex(".*?" + month + ".*");
        tcriteria.and("tired").is("✔");  //疲劳数据不为空
        Query tquery = new Query(tcriteria);
        long tcount = this.mongoTemplate.count(tquery, HGpsCarInfo.class);   //疲劳总量
        map.put("tcount", tcount);
        Criteria scriteria = new Criteria();
        scriteria.and("time").regex(".*?" + month + ".*");
        scriteria.and("speed").is("✔");   //超速数据不为空
        Query squery = new Query(scriteria);
        long scount = this.mongoTemplate.count(squery, HGpsCarInfo.class);   //超速总量
        map.put("scount", scount);
        Criteria ocriteria = new Criteria();
        ocriteria.and("month").regex(".*?" + month + ".*");
        ocriteria.and("online").is("是");
        Query oquery = new Query(ocriteria);
        long ocount = this.mongoTemplate.count(oquery, HGpsCarInfo.class);   //在线车辆总量
        map.put("ocount", ocount);
        Criteria ncriteria = new Criteria();
        ncriteria.and("month").regex(".*?" + month + ".*");
        Query nquery = new Query(ncriteria);
        long ncount = this.mongoTemplate.count(nquery, HGpsCarInfo.class);   //每月总在线数量
        System.out.println(ncount + "*****");
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后1位
        numberFormat.setMaximumFractionDigits(1);
        String ns = numberFormat.format((float) ocount / (float) ncount * 100) + "%";   //在线率计算
        map.put("ns", ns);
    }

    @Test
    public void testHGpsCarInfoService() {
//        Criteria  criteria=new Criteria();
////        criteria.and("wayTime").is("2019-09-29");
////        Query  query=new Query(criteria);
////        List<JGpsCarDetails> hGpsCarDetails = this.mongoTemplate.find(query, JGpsCarDetails.class);
////        System.out.println(hGpsCarDetails);
        HGpsCarDetails hGpsCarDetails = new HGpsCarDetails();
        hGpsCarDetails.setTime("2019-09-29");
        String time = "2019-09-29";
        Criteria criteria = Criteria.where("time").regex(".*?" + time + ".*");
        Query query = new Query(criteria);
        List<HGpsCarDetails> hGpsCarDetailss = this.mongoTemplate.find(query, HGpsCarDetails.class);
        System.out.println(hGpsCarDetailss);
        // Page<HGpsCarDetails> hGpsCarDetail = this.hGpsCarInfoService.getHGpsCarDetail(hGpsCarDetails, 1, 10);
        //System.out.println(hGpsCarDetail);
    }

    @Test
    public void testSimpleDateFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(calendar.DATE, -1);
        String date = sdf.format(calendar.getTime());
        System.out.println(date);
    }

    @Test
    public void testSimpleDateMonth() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        Date date = calendar.getTime();
        String mdate = format.format(date);
        System.out.println(mdate);
    }
    @Test
    public  void testRealPath( )throws   Exception{
        File file = new File("doc/报警处理明细.xlsx");
        System.out.println(file);
    }
}
