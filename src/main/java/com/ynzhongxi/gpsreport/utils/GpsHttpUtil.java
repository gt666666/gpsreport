package com.ynzhongxi.gpsreport.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import com.ynzhongxi.gpsreport.component.ConfigProperty;
import com.ynzhongxi.gpsreport.component.RedisUtils;
import com.ynzhongxi.gpsreport.pojo.LogJsession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/9/4
 */
@Component
public class GpsHttpUtil {
    @Resource
    private RedisUtils redis;
    @Resource
    private ConfigProperty property;

    public String getJsession() {
        String baseUrl = property.getGpsDataserviceHttp();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("account", "htgs");
        paramMap.put("password", "000000");
        String res = HttpUtil.post(baseUrl + "/StandardApiAction_login.action", paramMap);
        LogJsession logsession = JSONUtil.toBean(res, LogJsession.class);
        return logsession.getJsession();
    }

    public String get(String url, Map<String, Object> param) {
        Long time = System.currentTimeMillis();
        String baseUrl = property.getGpsDataserviceHttp();
        Object jsession = redis.get("jsession");
        param.put("jsession", null == jsession ? "" : jsession);
        StaticLog.info("#### {},HttpGet,URL:{},param:{}", time, url, param);
        try {
            String result = HttpUtil.get(baseUrl + url, param, 60 * 1000);
            JSONObject object = JSONUtil.parseObj(result);
            if (object.containsKey("result")) {
                Integer code = object.getInt("result");
                if (CollUtil.contains(CollUtil.toList(4, 5), code)) {
                    this.redis.set("jsession", this.getJsession());
                    result = get(url, param);
                }
            }
            StaticLog.info("#### {},耗时：{}", time, System.currentTimeMillis() - time);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

}
