package com.ynzhongxi.gpsreport.dao;

import com.ynzhongxi.gpsreport.pojo.GpsCarInfo;
import org.springframework.stereotype.Component;

@Component
public class GpsCarInfoDao extends BaseMongoDbDao {
    @Override
    protected Class getEntityClass() {
        return GpsCarInfo.class;
    }

}

