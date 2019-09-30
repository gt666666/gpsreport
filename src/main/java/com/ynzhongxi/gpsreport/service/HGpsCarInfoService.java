package com.ynzhongxi.gpsreport.service;

import com.ynzhongxi.gpsreport.dao.HGpsCarDetailsDAO;
import com.ynzhongxi.gpsreport.dao.HGpsCarInfoDao;
import com.ynzhongxi.gpsreport.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * by 罗毅涵
 * 车辆信息service类
 */
@Service
public class HGpsCarInfoService {
    @Autowired
    private final HGpsCarInfoDao gpsCarInfoDao;
    @Autowired
    private HGpsCarInfoDao hGpsCarInfoDao;
    @Autowired
    private HGpsCarDetailsDAO hGpsCarDetailsDAO;

    @Autowired
    public HGpsCarInfoService(HGpsCarInfoDao gpsCarInfoDao) {
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

    public Page<HGpsCarInfo> getListHGpsCarInfo(HGpsCarInfo gpsCarInfo, int page, int pageSize) {
        List<HGpsCarInfo> pageList = hGpsCarInfoDao.getPage(gpsCarInfo, page, pageSize);
        Long count = hGpsCarInfoDao.getLikeCount(gpsCarInfo);
        return new Page<>(page, pageSize, count, pageList);
    }

    public Map<String, Object> getHMonthCount(String month) {
        return this.hGpsCarInfoDao.getHMonthCount(month);
    }

    public Page<HGpsCarDetails> getHGpsCarDetailByTime(String  time, int page, int pageSize) {
        List<HGpsCarDetails> pageList = this.hGpsCarDetailsDAO.getPage(time, page, pageSize);
        Long count = hGpsCarDetailsDAO.getLikeCount(time);
        return new Page<>(page, pageSize, count, pageList);
    }
    public Page<HGpsCarDetails> getHGpsCarDetail(int page, int pageSize) {
        List<HGpsCarDetails> pageList = this.hGpsCarDetailsDAO.getPageDetails( page, pageSize);
        Long count = hGpsCarDetailsDAO.getDetalisLikeCount();
        return new Page<>(page, pageSize, count, pageList);
    }


}
