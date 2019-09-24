package com.ynzhongxi.gpsreport.dao;

import com.ynzhongxi.gpsreport.pojo.HGpsCarInfo;
import org.springframework.stereotype.Component;

@Component
public class GpsCarInfoDao extends BaseMongoDbDao {
    @Override
    protected Class getEntityClass() {
        return HGpsCarInfo.class;
    }



}

