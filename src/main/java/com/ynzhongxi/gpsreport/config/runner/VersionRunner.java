package com.ynzhongxi.gpsreport.config.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 项目启动后运行此方法:CommandLineRunner实现
 * 此方法用于版本记录，每次启用项目，都会执行一次该类的run方法
 *
 * @author lixingwu
 */
@Slf4j
@Component
@Order(value = 1)
public class VersionRunner implements CommandLineRunner {

    /**
     * 启用的配置
     */
    @Value("${spring.profiles.active}")
    String active;

    /**
     * 项目启动就执行
     */
    @Override
    public void run(String... args) {
        log.info(">>>>项目初始化完成！<<<<");
        // 打印加载的配置文件是哪一个
        log.info(">>>>加载了配置文件 application-{}.yml", active);
    }
}
