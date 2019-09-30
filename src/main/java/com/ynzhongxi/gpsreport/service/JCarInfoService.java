package com.ynzhongxi.gpsreport.service;

import com.ynzhongxi.gpsreport.dao.HCarInfoDao;
import com.ynzhongxi.gpsreport.dao.JCarInfoDao;
import com.ynzhongxi.gpsreport.pojo.HCarInfo;
import com.ynzhongxi.gpsreport.pojo.JCarInfo;
import com.ynzhongxi.gpsreport.pojo.JGpsCarInfo;
import com.ynzhongxi.gpsreport.pojo.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/9/29
 */
@Service
public class JCarInfoService {
    @Resource
    private JCarInfoDao jCarInfoDao;

    public void editByCarNumber(JCarInfo jCarInfo) {
        this.jCarInfoDao.updateFirst(jCarInfo);
    }

    public Page<JCarInfo> listCarInfo(JCarInfo jCarInfo, int page, int pageSize) {
        List<JCarInfo> PageList = this.jCarInfoDao.getPage(jCarInfo, page, pageSize);
        Long count = jCarInfoDao.getLikeCount(jCarInfo);
        return new Page<>(page, pageSize, count, PageList);
    }

    public JCarInfo carInfoById(String  id) {
        return this.jCarInfoDao.queryById(id);
    }

    public int deleteByCarnumber(JCarInfo jCarInfo) {
        return this.jCarInfoDao.delete(jCarInfo);
    }

    public void insertJCarInfo(JCarInfo jCarInfo) {
        this.jCarInfoDao.save(jCarInfo);
    }
}
