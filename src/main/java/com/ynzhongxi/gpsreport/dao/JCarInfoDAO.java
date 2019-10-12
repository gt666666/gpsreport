package com.ynzhongxi.gpsreport.dao;

import com.ynzhongxi.gpsreport.pojo.JCarInfo;
import org.springframework.stereotype.Component;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/9/29
 */
@Component
public class JCarInfoDAO extends BaseMongoDbDao<JCarInfo> {
    @Override
    protected Class<JCarInfo> getEntityClass() {
        return JCarInfo.class;
    }
    public void updateFirst(JCarInfo jCarInfo) {
        String  carNumber=jCarInfo.getCarNumber();
        super.updateFirst(carNumber,jCarInfo);
    }
}
