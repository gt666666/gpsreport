package com.ynzhongxi.gpsreport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;

@SpringBootApplication
@EnableScheduling
public class GpsreportApplication {

    public static void main(String[] args) {
        SpringApplication.run(GpsreportApplication.class, args);
    }

}
