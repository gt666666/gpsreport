package com.ynzhongxi.gpsreport.config.convert;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 序列化为BigDecimal数据保留2位小数
 * @author lixingwu
 */
public class CustomBigDecimalChange extends JsonSerializer<BigDecimal> {
    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        DecimalFormat bfFormat = new DecimalFormat("0.00");
        gen.writeString(bfFormat.format(value));
    }
}

