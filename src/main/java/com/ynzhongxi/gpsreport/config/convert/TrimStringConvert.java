package com.ynzhongxi.gpsreport.config.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;


/**
 * 字符串转换器，去除前后空格
 *
 * @author lixingwu
 */
public class TrimStringConvert implements Converter<String, String> {
    @Override
    public String convert(String params) {
        return StringUtils.trimLeadingWhitespace(params);
    }
}
