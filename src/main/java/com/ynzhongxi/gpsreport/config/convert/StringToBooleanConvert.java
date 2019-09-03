package com.ynzhongxi.gpsreport.config.convert;

import org.springframework.core.convert.converter.Converter;

/**
 * 把一些字符串转换为 Boolean 类型
 *
 * @author lixingwu
 */
public class StringToBooleanConvert implements Converter<String, Boolean> {
    @Override
    public Boolean convert(String params) {
        switch (params) {
            case "":
                return false;
            case "on":
                return true;
            case "true":
                return true;
            case "false":
                return false;
            case "1":
                return true;
            case "0":
                return false;
            default:
                return null;
        }
    }
}
