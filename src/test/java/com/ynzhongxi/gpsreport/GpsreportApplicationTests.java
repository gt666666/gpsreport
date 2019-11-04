package com.ynzhongxi.gpsreport;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.ynzhongxi.gpsreport.pojo.HGpsCarInfo;
import com.ynzhongxi.gpsreport.service.HGpsCarInfoService;
import com.ynzhongxi.gpsreport.utils.Tools;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RunWith(SpringRunner.class)
@SpringBootTest
public class GpsreportApplicationTests {

    @Autowired
    private HGpsCarInfoService gpsCarInfoService;

    @Test
    public void contextLoads() {
    }

//    @Test
    public void convert() {
        //请求路径
        String path = "http://60.161.53.204:8088";
        String url = "/StandardApiAction_queryUserVehicle.action";
        //创建数据参数
        Map<String, Object> map = new HashMap<>();
        map.put("jsession","8cf03a4e-34d9-438e-ae65-b5f38acaba53");
        String httpUrl = StrUtil.format("{}{}{}", path, url, Tools.mapToUrl(map));
        System.err.println("查询的json数据url：" + httpUrl);
        String post = HttpUtil.post(httpUrl, map);
        System.err.println("查询的json数据：" + post);
        JSONObject jsonObject = JSONUtil.parseObj(post);
        JSONArray json = jsonObject.getJSONArray("vehicles");
        //读取Excel数据比对
        ExcelReader reader = ExcelUtil.getReader("C:/myfile/ht.xls");
        List<Map<String,Object>> readAll = reader.readAll();
        //读取Excel数据比对
        ExcelReader readerw = ExcelUtil.getReader("C:/myfile/htw.xls");
        List<Map<String,Object>> readAllw = readerw.readAll();
        int number = 0;
        HGpsCarInfo carInfo;
        for (int i =0; i < json.size(); i++) {
           JSONObject object = json.getJSONObject(i);
            String nm = object.getStr("nm");
//            System.out.println(i+"车牌号：" + nm);
            String id = object.getJSONArray("dl").getJSONObject(0).getStr("id");
//            System.out.println(i+"设备号i：" + id);
            carInfo = new HGpsCarInfo();
            carInfo.setCarNumber(nm);
            carInfo.setDeviceId(id);
            //循环比对Excel数据
            for (int j = 0; j < readAll.size(); j++) {
                Map<String, Object> objectMap = readAll.get(j);
                //判断车牌号是否相等
                if (null != objectMap.get("carNumber") && nm.equals(objectMap.get("carNumber").toString())) {
                    //获取信息
                    //判断姓名是否为空
                    if (null != objectMap.get("ower").toString()) {
                        carInfo.setDriverName(objectMap.get("ower").toString());
                    } else {
                        carInfo.setDriverName(objectMap.get("driver").toString());
                    }
                    //判断电话
                    if (null != objectMap.get("phone").toString()) {
                        carInfo.setPhone(objectMap.get("phone").toString());
                    } else {
                        carInfo.setPhone(objectMap.get("telephone").toString());
                    }
                }
            }
            //判断是否还需要比较填充
            if (null == carInfo.getDriverName() || null ==carInfo.getPhone()) {
                for (int j = 0; j < readAllw.size(); j++) {
                    Map<String, Object> objectMap = readAllw.get(j);
                    //判断车牌号是否相等
                    if (null != objectMap.get("carNumber") && nm.equals(objectMap.get("carNumber").toString())) {
                        //获取信息
                        //判断姓名是否为空
                        if (null != objectMap.get("name").toString()) {
                            carInfo.setDriverName(objectMap.get("name").toString());
                        }
                        //判断电话
                        if (null != objectMap.get("phone").toString()) {
                            carInfo.setPhone(objectMap.get("phone").toString());
                        }
                    }
                }
            }
            gpsCarInfoService.saveCar(carInfo);
            System.err.println(i+"车辆信息保存成功"+carInfo.toString());

        }
        System.err.println("空数据:"+number);

    }
    @Test
    public void getCarInformation() {
        List<HGpsCarInfo> gpsCarInfos = gpsCarInfoService.carList(new HGpsCarInfo());
        for (HGpsCarInfo gpsCarInfo: gpsCarInfos){
            System.err.println(gpsCarInfo);
        }
    }

}
