package com.ynzhongxi.gpsreport.component;

import com.ynzhongxi.gpsreport.BaseSpringBootTest;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.*;

public class RedisUtilsTest extends BaseSpringBootTest {
    @Resource
    RedisUtils redis;

    @Test
    public void getExpire() throws InterruptedException {
        print(redis.set("ha", "ha", 10));
        Thread.sleep(2000);
        print(redis.getExpire("ha"));
    }

    @Test
    public void hasKey() throws InterruptedException {
        print(redis.set("key001", "key001", 1));
        Thread.sleep(1000);
        print(redis.hasKey("key001"));
    }

    @Test
    public void incr() {
        redis.set("key001", 3L);
        print(redis.incr("key001", 1L));
        print(redis.incr("incr001"));
    }

    @Test
    public void decr() {
        print(redis.decr("key001", 1L));
        print(redis.decr("incr001"));
    }

    @Override
    public String baseUrl() {
        return null;
    }
}