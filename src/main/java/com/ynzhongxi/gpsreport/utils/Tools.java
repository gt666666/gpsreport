package com.ynzhongxi.gpsreport.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * The type Tools.
 *
 * @author lixingwu
 */
public class Tools {

    /**
     * 方法描述：获取资源文件转化为Map<String, String>
     * 创建时间：2019-06-13 11:27:37
     * 创建作者：李兴武
     *
     * @param path resources资源的相对路径
     * @return the properties
     * @author "lixingwu"
     */
    public static Map<String, String> getProperties(String path) {
        Properties properties = new Properties();
        InputStream resource = null;
        InputStreamReader reader = null;
        Map<String, String> map = new HashMap<>(10);
        try {
            resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            if (null != resource) {
                reader = new InputStreamReader(resource, StandardCharsets.UTF_8);
                properties.load(reader);

                Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();
                for (Map.Entry<Object, Object> entry : entrySet) {
                    if (!entry.getKey().toString().startsWith("#")) {
                        map.put(((String) entry.getKey()).trim(), ((String) entry.getValue()).trim());
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (resource != null) {
                try {
                    resource.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }

        return map;
    }

    /**
     * 方法描述：获取项目资源的绝对路径.
     * 创建时间：2019-06-12 23:57:46
     * 创建作者：李兴武
     *
     * @return the string
     * @author "lixingwu"
     */
    public static String getRootPath() {
        return ClassUtil.getClassPath();
    }

    public static boolean isString(String str) {
        if (str == null || "".equals(str)) {	// 验证失败
            return false ;
        }
        return true ;
    }

    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * <p>
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     * <p>
     * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
     * 192.168.1.100
     * <p>
     * 用户真实IP为： 192.168.1.110
     *
     * @param request the request
     * @return string
     * @author "lixingwu"
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        String unknown = "unknown";
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 方法描述：自定义判断是否为空
     * 创建作者：李兴武
     * 创建日期：2017-06-22 19:50:01
     *
     * @param str the str
     * @return the boolean
     * @author "lixingwu"
     */
    public static Boolean isBlank(String str) {
        if (str != null) {
            str = str.replaceAll("\r\n|\n\r|\n|\r|\f|\t", "");
        }
        if (str == null) {
            return true;
        } else if ("".equals(str)) {
            return true;
        } else if ("null".equals(str)) {
            return true;
        } else if ("NULL".equals(str)) {
            return true;
        } else if ("(null)".equals(str)) {
            return true;
        } else if ("(NULL)".equals(str)) {
            return true;
        } else {
            return str.trim().length() == 0;
        }
    }

    /**
     * 方法描述：判断obj是否为空
     * 创建作者：李兴武
     * 创建日期：2017-06-22 19:50:01
     *
     * @param obj the 判断的对象
     * @return the boolean
     * @author "lixingwu"
     */
    public static Boolean isBlank(Object obj) {
        if (obj != null) {
            return isBlank(String.valueOf(obj));
        }
        return true;
    }

    /**
     * 字节b转化为 kb、mb、gb
     *
     * @param size 字节数大小
     * @return string
     * @author "lixingwu"
     */
    public static String bit2KMG(long size) {
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1024) {
            return size + "B";
        } else {
            size = size / 1024;
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1024) {
            return size + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            size = size * 100;
            return size / 100 + "." + size % 100 + "MB";
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return size / 100 + "." + size % 100 + "GB";
        }
    }

    /**
     * 方法描述：把map参数转化为URL参数字符串.
     * 创建时间：2018-11-25 22:30:11
     *
     * @param map 参数Map
     * @return the string
     * @author "lixingwu"
     */
    public static String mapToUrl(Map map) {
        StringBuilder sb = new StringBuilder("?");
        if (map.size() > 0) {
            for (Object key : map.keySet()) {
                sb.append(key).append("=");
                if (Tools.isBlank(map.get(key))) {
                    sb.append("&");
                } else {
                    Object value = map.get(key);
                    try {
                        value = URLEncoder.encode(Convert.toStr(value), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    sb.append(value).append("&");
                }
            }
        }

        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;

    }

    /**
     * 方法描述：截取数字
     * 创建时间：2019-03-22 09:19:19
     * 创建作者：刘万琼
     *
     * @param content the content
     * @return the string
     */
    public static String getNumber(String content) {
        String regEx = "[^0-9]";
        Pattern pattern = compile(regEx);
        Matcher matcher = pattern.matcher(content);
        return matcher.replaceAll("").trim();
    }

    /**
     * 获取客户端请求参数中所有的信息
     */
    public static Map<String, String> getAllRequestParam(HttpServletRequest request) {
        Map<String, String> res = new HashMap<>(10);
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                res.put(en, value);
                //如果字段的值为空，判断若值为空，则删除这个字段>
                if (null == res.get(en) || "".equals(res.get(en))) {
                    res.remove(en);
                }
            }
        }
        return res;
    }

    /**
     * <p> 方法描述：获取客户端的Header. </p>
     * <p> 创建时间：2019-06-28 15:10:07 </p>
     * <p> 创建作者：李兴武 </p>
     *
     * @param request the request
     * @return the map
     * @author "lixingwu"
     */
    public static Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>(5);
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

    /**
     * <p> 方法描述：把request的body内容转换为HashMap. </p>
     * <p> 创建时间：2019-06-28 15:33:39 </p>
     * <p> 创建作者：李兴武 </p>
     *
     * @param body the request
     * @return the hash map
     * @author "lixingwu"
     */
    public static HashMap<String, String> getBodyParamMap(String body) {
        if (!isBlank(body)) {
            return HttpUtil.decodeParamMap(body, CharsetUtil.UTF_8);
        }
        return new HashMap<>(1);
    }

    /**
     * 方法描述：向 response 写文本字符串.
     * 创建时间：2019-06-23 00:11:02
     * 创建作者：李兴武
     *
     * @param response the response
     * @param text     要写入的文本
     * @author "lixingwu"
     */
    public static void writeText(HttpServletResponse response, String text) {
        ServletUtil.write(response, text, ContentType.TEXT_XML.toString());
    }

    /**
     * 方法描述：向 response 写JSON字符串..
     * 创建时间：2019-06-23 00:11:44
     * 创建作者：李兴武
     *
     * @param response the response
     * @param object   要写入的对象，调用toJsonStr后输出的
     * @author "lixingwu"
     */
    public static void writeJson(HttpServletResponse response, Object object) {
        String text = JSONUtil.toJsonStr(object);
        ServletUtil.write(response, text, ContentType.JSON.toString());
    }
    public  static NumberFormat getNumberFormat(){
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后1位
        numberFormat.setMaximumFractionDigits(1);
        return   numberFormat;
    }
}
