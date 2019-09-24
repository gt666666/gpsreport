package com.ynzhongxi.gpsreport.service;

import com.ynzhongxi.gpsreport.dao.GpsCarInfoDao;
import com.ynzhongxi.gpsreport.pojo.HGpsCarInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * by 罗毅涵
 * 车辆信息service类
 */
@Service
public class GpsCarInfoService {
    private final GpsCarInfoDao gpsCarInfoDao;
    @Autowired
    public GpsCarInfoService(GpsCarInfoDao gpsCarInfoDao) {
        this.gpsCarInfoDao = gpsCarInfoDao;
    }

    /**
     * <p> 方法描述：保存车辆信息 </p >
     * <p> 创建时间：2019-09-04 10:49:58 </p >
     * <p> 创建作者：罗毅涵 </p >
     * <p> 修改作者： </p >
     *
     * @param gpsCarInfo the gps car info
     */
    public void saveCar(HGpsCarInfo gpsCarInfo) {
        gpsCarInfoDao.save(gpsCarInfo);
    }

    /**
     * <p> 方法描述：查询所有车辆信息 </p >
     * <p> 创建时间：2019-09-05 09:13:11 </p >
     * <p> 创建作者：罗毅涵 </p >
     * <p> 修改作者： </p >
     *
     * @param gpsCarInfo the gps car info
     * @return the list
     */
    public List<HGpsCarInfo> carList(HGpsCarInfo gpsCarInfo) {
        return gpsCarInfoDao.queryList(gpsCarInfo);
    }
}
