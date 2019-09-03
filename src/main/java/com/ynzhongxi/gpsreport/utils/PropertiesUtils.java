package com.ynzhongxi.gpsreport.utils;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 读取properties文件
 *
 * @author lixingwu
 */
public class PropertiesUtils {
    private Properties properties;
    private Properties propertiesCustom;
    private static PropertiesUtils propertiesUtils = new PropertiesUtils();

    /**
     * 私有构造，禁止直接创建
     */
    private PropertiesUtils() {
        // 读取配置启用的配置文件名
        properties = new Properties();
        propertiesCustom = new Properties();
        InputStream in = PropertiesUtils.class.getClassLoader().getResourceAsStream("custom.properties");
        try {
            properties.load(in);
            // 加载启用的配置
            String property = properties.getProperty("profiles.active");
            if (!Tools.isBlank(property)) {
                InputStream cin = PropertiesUtils.class.getClassLoader().getResourceAsStream("custom-" + property + ".properties");
                propertiesCustom.load(cin);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取单例
     *
     * @return PropertiesUtils
     */
    public static PropertiesUtils getInstance() {
        if (propertiesUtils == null) {
            propertiesUtils = new PropertiesUtils();
        }
        return propertiesUtils;
    }

    /**
     * 根据属性名读取值
     * 先去主配置查询，如果查询不到，就去启用配置查询
     *
     * @param name 名称
     */
    public String getProperty(String name) {
        String val = properties.getProperty(name);
        if (Tools.isBlank(val)) {
            val = propertiesCustom.getProperty(name);
        }
        return val;
    }

    public static void main(String[] args) {
        PropertiesUtils pro = PropertiesUtils.getInstance();
        String value = pro.getProperty("custom.properties.name");
        System.out.println(value);
    }
}
