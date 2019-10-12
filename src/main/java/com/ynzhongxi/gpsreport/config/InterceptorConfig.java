package com.ynzhongxi.gpsreport.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * 拦截器配置类
 *
 * @author lixingwu
 */
@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {
    @Bean
    public SessionInterceptors sessionInterceptor() {
        return new SessionInterceptors();
    }
    /***
     * 添加一个拦截器
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/logout")
                .excludePathPatterns("/login").excludePathPatterns("/ImageCode");

        super.addInterceptors(registry);
    }
}
