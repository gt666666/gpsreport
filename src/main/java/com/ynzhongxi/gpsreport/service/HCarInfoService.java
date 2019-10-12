package com.ynzhongxi.gpsreport.service;

import com.ynzhongxi.gpsreport.dao.HCarInfoDAO;
import com.ynzhongxi.gpsreport.pojo.HCarInfo;
import com.ynzhongxi.gpsreport.pojo.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.List;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/9/29
 */
@Service
public class HCarInfoService {

    @Resource
    private HCarInfoDAO hCarInfoDao;

    public void editByCarNumber(HCarInfo hCarInfo) {
        this.hCarInfoDao.updateFirst(hCarInfo);
    }

    public Page<HCarInfo> listCarInfo(HCarInfo hCarInfo, @RequestParam() int page, int pageSize) {
        List<HCarInfo> PageList = this.hCarInfoDao.getPage(hCarInfo, page, pageSize);
        Long count = hCarInfoDao.getLikeCount(hCarInfo);
        return new Page<>(page, pageSize, count, PageList);
    }

    public HCarInfo carInfoById(String  id) {
        System.out.println("**************"+id);
        return this.hCarInfoDao.queryById(id);
    }

    public int deleteByCarnumber(HCarInfo hCarInfo) {
        return this.hCarInfoDao.delete(hCarInfo);
    }

    public    void   insertHCarInfo(HCarInfo  hCarInfo){
        this.hCarInfoDao.save(hCarInfo);
    }
}
