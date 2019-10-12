package com.ynzhongxi.gpsreport.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/10/9
 */
public class CookieUtil {
    /**
     * <p>方法名称：Save.</p >
     * <p>详细描述：定义一个专门负责保存Cookie的方法，可以设置Cookie的名字与保存时间.</p >
     * <p>创建时间：2019-10-09 16:44:36</p >
     * <p>创建作者：高挺</p >
     * <p>修改记录：</p >
     *
     * @param response 主要可以使用addCookie()方法保存Cookie对象
     * @param request  主要设置Cookie的保存路劲，如果不设置保存不上
     * @param name     保存的Cookie名字
     * @param value    保存的Cookie的内容
     * @param expiry   Cookie的失效时间
     * @author "gaoting"
     */
    public static void save(HttpServletResponse response, HttpServletRequest request, String name, String value, int expiry) {
        Cookie c = new Cookie(name, value);
        c.setMaxAge(expiry);   //过期时间
        c.setPath(request.getContextPath());  //设置保存路径
        response.addCookie(c);
    }

    //去除全部Cookie的操作方法
    public static Map<String, String> load(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Cookie[] c = request.getCookies();
        for (int x = 0; x < c.length; x++) {
            map.put(c[x].getName(), c[x].getValue());
        }
        return map;

    }

}
