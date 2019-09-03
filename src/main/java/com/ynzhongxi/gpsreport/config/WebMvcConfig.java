package com.ynzhongxi.gpsreport.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ynzhongxi.gpsreport.config.convert.DateConverter;
import com.ynzhongxi.gpsreport.config.convert.StringToBooleanConvert;
import com.ynzhongxi.gpsreport.config.convert.TrimStringConvert;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.List;

/**
 * 自定义配置.
 *
 * @author lixingwu
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Resource
    private RequestMappingHandlerAdapter handlerAdapter;

    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    /**
     * responseBodyConverter和MappingJackson2HttpMessageConverter如果分开配置要确保前者不被覆盖，
     * 不然就会出现：
     * 返回springboot返回json正常，但是返回中文乱码；
     * 或者返回中文不乱码，但是返回对象或者json异常；
     */
    @Bean
    public MappingJackson2HttpMessageConverter messageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(getObjectMapper());
        return converter;
    }

    /**
     * 设置MessageConverters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        converters.add(messageConverter());
    }

    /**
     * 配置转换器
     */
    @PostConstruct
    public void initEditableAvlidation() {
        ConfigurableWebBindingInitializer initializer = (ConfigurableWebBindingInitializer) handlerAdapter.getWebBindingInitializer();
        GenericConversionService genericConversionService = (GenericConversionService) initializer.getConversionService();
        genericConversionService.addConverter(new TrimStringConvert());
        genericConversionService.addConverter(new DateConverter());
        genericConversionService.addConverter(new StringToBooleanConvert());
    }
}
