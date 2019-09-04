package com.ynzhongxi.gpsreport;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ynzhongxi.gpsreport.utils.Tools;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GpsreportApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void convert() {
        //请求路径
        String path = "http://60.161.53.204:8088";
        String url = "/StandardApiAction_queryUserVehicle.action";
        //创建数据参数
        Map<String, Object> map = new HashMap<>();
        map.put("jsession","f117d3f7-d3f8-4538-a8d5-1b2f2917376e");
        String httpUrl = StrUtil.format("{}{}{}", path, url, Tools.mapToUrl(map));
        System.err.println("查询的json数据url：" + httpUrl);
        String post = HttpUtil.post(httpUrl, map);
        System.err.println("查询的json数据：" + post);
        JSONObject jsonObject = JSONUtil.parseObj(post);
        JSONArray json = jsonObject.getJSONArray("vehicles");
        for (int i =0; i < json.size(); i++) {
           JSONObject object = json.getJSONObject(i);
            String nm = object.getStr("nm");
            System.out.println(i+"车牌号：" + nm);
            String id = object.getJSONArray("dl").getJSONObject(0).getStr("id");
            System.out.println(i+"设备号i：" + id);
        }

    }

}
