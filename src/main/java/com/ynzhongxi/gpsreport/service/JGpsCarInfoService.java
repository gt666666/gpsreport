package com.ynzhongxi.gpsreport.service;

import com.ynzhongxi.gpsreport.dao.JGpsCarDetailsDAO;
import com.ynzhongxi.gpsreport.dao.JGpsCarInfoDAO;
import com.ynzhongxi.gpsreport.pojo.JGpsCarDetails;
import com.ynzhongxi.gpsreport.pojo.JGpsCarInfo;
import com.ynzhongxi.gpsreport.pojo.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/9/29
 */
@Service
public class JGpsCarInfoService {
    @Autowired
    private JGpsCarInfoDAO jGpsCarInfoDao;
    @Autowired
    private JGpsCarDetailsDAO jGpsCarDetailsDAO;

    public Page<JGpsCarInfo> getListJGpsCarInfo(JGpsCarInfo jgpsCarInfo, int page, int pageSize) {
        List<JGpsCarInfo> pageList = jGpsCarInfoDao.getPage(jgpsCarInfo, page, pageSize);
        Long count = jGpsCarInfoDao.getLikeCount(jgpsCarInfo);
        return new Page<>(page, pageSize, count, pageList);
    }

    public Map<String, Object> getJMonthCount(String month) {
        return this.jGpsCarInfoDao.getJMonthCount(month);
    }

    public Page<JGpsCarDetails> getHGpsCarDetailByTime(String time, int page, int pageSize,String type) {
        List<JGpsCarDetails> pageList = jGpsCarDetailsDAO.getPage(time,type, page, pageSize);
        Long count = jGpsCarDetailsDAO.getLikeCount(time,type);
        return new Page<>(page, pageSize, count, pageList);
    }

    public Page<JGpsCarDetails> getJGpsCarDetail(int page, int pageSize) {
        List<JGpsCarDetails> pageList = this.jGpsCarDetailsDAO.getPageDetails(page, pageSize);
        Long count = jGpsCarDetailsDAO.getDetalisLikeCount();
        return new Page<>(page, pageSize, count, pageList);
    }
}
