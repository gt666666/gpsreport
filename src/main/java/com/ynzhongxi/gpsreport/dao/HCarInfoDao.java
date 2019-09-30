package com.ynzhongxi.gpsreport.dao;
import com.ynzhongxi.gpsreport.pojo.HCarInfo;
import org.springframework.stereotype.Component;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/9/29
 */
@Component
public class HCarInfoDao extends BaseMongoDbDao<HCarInfo> {
    @Override
    protected Class<HCarInfo> getEntityClass() {
        return HCarInfo.class;
    }
    public void updateFirst(HCarInfo hCarInfo) {
        String  carNumber=hCarInfo.getCarNumber();
        super.updateFirst(carNumber,hCarInfo);
    }
}
