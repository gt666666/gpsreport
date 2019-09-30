package com.ynzhongxi.gpsreport.dao;

import com.ynzhongxi.gpsreport.pojo.Test;
import org.springframework.stereotype.Component;

/**
 * 数据库操作测试
 *
 * @author "lixingwu"
 */
@Component
public class TestDao extends BaseMongoDbDao<Test> {
    @Override
    protected Class<Test> getEntityClass() {
        return Test.class;
    }
}
