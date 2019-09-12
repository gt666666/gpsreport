package com.ynzhongxi.gpsreport;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.ynzhongxi.gpsreport.component.RedisUtils;
import com.ynzhongxi.gpsreport.pojo.CarInfo;
import com.ynzhongxi.gpsreport.pojo.Course;
import com.ynzhongxi.gpsreport.pojo.GpsCarInfo;
import com.ynzhongxi.gpsreport.pojo.Student;
import com.ynzhongxi.gpsreport.utils.DateFormatUtil;
import com.ynzhongxi.gpsreport.utils.GspHttpUtil;
import com.ynzhongxi.gpsreport.utils.JxlsUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ExecutableRemoveOperation;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.OutputStream;
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
    private GspHttpUtil gspHttpUtil;
    @Autowired
    private MongoTemplate mongoTemplate;
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
        System.out.println(gspHttpUtil.get("/StandardApiAction_queryAlarmDetail.action", map));
    }

    @Test
    public void insertGpsCarInfo() {
        this.mongoTemplate.dropCollection("gpsCarInfo");
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("driverName").exists(true), Criteria.where("driverName").is(""));   //条件为:驾驶员姓名不为空
        Query query = new Query(criteria);
        List<CarInfo> carInfos = this.mongoTemplate.find(query, CarInfo.class);   //查询出所有驾驶员姓名不为空的数据
        Iterator<CarInfo> iter = carInfos.iterator();
        List<GpsCarInfo> gpsCarInfos = new ArrayList<>();    //保存所有驾驶员的台账信息
        while (iter.hasNext()) {
            CarInfo carInfo = iter.next();
            GpsCarInfo gpsCarInfo = new GpsCarInfo();
            gpsCarInfo.setCarNumber(carInfo.getCarNumber());
            gpsCarInfo.setDriverName(carInfo.getDriverName());
            gpsCarInfo.setPhone(carInfo.getPhone());
            Map<String, Object> map = new HashMap<>();
            map.put("devIdno", carInfo.getDeviceId());   //设备号
            String one = gspHttpUtil.get("/StandardApiAction_getDeviceOlStatus.action", map);  //获取设备在线状态
            JSONObject json1 = JSONUtil.parseObj(one);
            if (json1.containsKey("onlines")) {  //判断返回的JSON数据中是否有onlines的JSON数组
                JSONArray onlines = json1.getJSONArray("onlines");
                JSONObject online = onlines.getJSONObject(0);
                int on = online.getInt("online");
                if (on == 1) {
                    gpsCarInfo.setOnline("是"); //GPS在线
                } else {
                    gpsCarInfo.setOnline("否");   //GPS不在线
                }
            }
            map.put("vehiIdno", carInfo.getCarNumber());  //车牌号
            map.put("geoaddress", 1);//解析最新地理位置
            String in = gspHttpUtil.get("/StandardApiAction_vehicleStatus.action", map);//获取车辆最新位置
            JSONObject json2 = JSONUtil.parseObj(in);
            String infoStr = json2.getStr("infos");
            if (JSONUtil.isJsonArray(infoStr)) {
                JSONObject info = JSONUtil.parseArray(infoStr).getJSONObject(0);
                if (info.getStr("pos") != "" && info.getStr("tm") != null) {   //判断返回的地理位置不能为空、最后在线时间不能为null
                    gpsCarInfo.setPos(info.getStr("pos"));          //地理位置
                    if (new Date().getTime() - 86220000L <= info.getLong("tm") && info.getLong("tm") <= new Date().getTime()) {     //每晚23:57统计最后在线时间,当天时间范围00:00~23:57
                        gpsCarInfo.setTime(DateFormatUtil.simpleDate(info.getLong("tm")));   //当天最后在线时间
                    }
                }
            }
            if ("是".equals(gpsCarInfo.getOnline()) && null == gpsCarInfo.getTime()) {
                gpsCarInfo.setType(1);    //GPS在线，但是返回时间为空，则车台故障
            } else {
                gpsCarInfo.setType(0);   //车台没有故障
            }
            map.put("begintime", DateFormatUtil.simpleDate(new Date().getTime() - 86220000L));  //开始时间
            map.put("endtime", DateFormatUtil.simpleDate(new Date().getTime()));       //结束时间
            map.put("armType", "11,61,178,180,200,222,223,224,225,226,227,228,229,230,304,309,311,314,99,125,249,299,306,49,99,125,249,299,306,618,619"); //报警类型:  疲劳：49,99,125,249,299,306,618,619
            map.put("pageRecords", 100);   //显示前100条记录                                                      //超速：11,61,178,180,200,222,223,224,225,226,227,228,229,230,304,309,311,314
            map.put("toMap", 2);  //地图经纬度转换  2：百度地图解析可以解析出地址
            String baoJin = gspHttpUtil.get("/StandardApiAction_queryAlarmDetail.action", map); //获取设备超速、疲劳驾驶报警数据
            JSONObject json3 = JSONUtil.parseObj(baoJin);
            String alarmsStr = json3.getStr("alarms");
            gpsCarInfo.setTired(0);     //无疲劳
            gpsCarInfo.setSpeed(0);    //无超速
            if (JSONUtil.isJsonArray(alarmsStr)) {
                JSONArray alarms = JSONUtil.parseArray(alarmsStr);
                for (int i = 0; i < alarms.size(); i++) {
                    int atp = alarms.getJSONObject(i).getInt("atp");   //报警类型
                    if (CollUtil.toList(11, 61, 178, 180, 200, 222, 223, 224, 225, 226, 227, 228, 229, 230, 304, 309, 311, 314).contains(atp)) {
                        gpsCarInfo.setSpeed(1);   //超速
                        gpsCarInfo.setData("有");    //有报警数据
                    }
                    if (CollUtil.toList(49, 99, 125, 249, 299, 306, 618, 619).contains(atp)) {
                        gpsCarInfo.setTired(1);   //疲劳
                        gpsCarInfo.setData("有");    //有报警数据
                    }
                }
            } else {
                gpsCarInfo.setData("无");    //无报警数据
            }
            String sps = gspHttpUtil.get("/StandardApiAction_getDeviceStatus.action", map);   //获取车辆速度
            JSONObject json4 = JSONUtil.parseObj(sps);
            String statusStr = json4.getStr("status");
            if (JSONUtil.isJsonArray(statusStr)) {
                JSONObject status = JSONUtil.parseArray(statusStr).getJSONObject(0);
                String sp = status.getStr("sp");
                if (sp != null) {     //速度内容不能为空
                    gpsCarInfo.setSp(Integer.parseInt(sp) / 10.0);
                }
            }
            gpsCarInfos.add(gpsCarInfo);
        }
        this.mongoTemplate.insertAll(gpsCarInfos);   //将所有司机的gps信息插入到数据库
    }

    @Test
    public void getGpsCarInfo() throws Exception {
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("time").exists(true), Criteria.where("time").is(""));   //time不为空的所有数据
        Query query = new Query(criteria);
        List<GpsCarInfo> gpsCarInfos = this.mongoTemplate.find(query, GpsCarInfo.class);
        Map<String, Object> map = new HashMap<>();
        map.put("gpsCarInfos", gpsCarInfos);
        // 模板路径和输出流
        String templatePath = "E:\\jxls\\studentTemplate.xlsx";
        OutputStream os = new FileOutputStream("E:\\jxls\\student.xls");
        //调用封装的工具类，传入模板路径，输出流，和装有数据的Map,按照模板导出
        JxlsUtil.exportExcel(templatePath, os, map);
        os.close();
    }
    @Test
    public   void  Excel() throws Exception {
        List<Student> students = new ArrayList<>();
        Student student = new Student();
        student.setName("张三");
        student.setGender("男");
        student.setGradeClass("初一一班");
        List<Course> courses = new ArrayList<>();
        Course course = new Course();
        course.setCourseName("语文");
        course.setCourseScore("98");
        courses.add(course);
        course = new Course();
        course.setCourseName("数学");
        course.setCourseScore("105");
        courses.add(course);
        course = new Course();
        course.setCourseName("物理");
        course.setCourseScore("80");
        courses.add(course);

        student.setCourses(courses);
        students.add(student);


        student = new Student();
        student.setName("王丽丽");
        student.setGender("女");
        student.setGradeClass("初一二班");
        courses = new ArrayList<>();
        course = new Course();
        course.setCourseName("语文");
        course.setCourseScore("102");
        courses.add(course);
        course = new Course();
        course.setCourseName("数学");
        course.setCourseScore("110");
        courses.add(course);
        student.setCourses(courses);
        students.add(student);

        student = new Student();
        student.setName("李梅");
        student.setGender("女");
        student.setGradeClass("初一三班");
        courses = new ArrayList<>();
        course = new Course();
        course.setCourseName("语文");
        course.setCourseScore("110");
        courses.add(course);
        course = new Course();
        course.setCourseName("数学");
        course.setCourseScore("100");
        courses.add(course);
        course = new Course();
        course.setCourseName("物理");
        course.setCourseScore("85");
        courses.add(course);
        student.setCourses(courses);
        students.add(student);
        //模板里展示的数据
        Map<String, Object> data = new HashMap<>();
        data.put("students", students);
        data.put("AAAA","握手");
        System.out.println(data.get("students"));
        // 模板路径和输出流
        String templatePath = "E:\\jxls\\studentTemplate.xls";
        OutputStream os = new FileOutputStream("E:\\jxls\\student.xls");
        //调用封装的工具类，传入模板路径，输出流，和装有数据的Map,按照模板导出
        JxlsUtil.exportExcel(templatePath, os, data);
    }

}
