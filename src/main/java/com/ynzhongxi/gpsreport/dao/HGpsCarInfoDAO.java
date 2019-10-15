package com.ynzhongxi.gpsreport.dao;

import cn.hutool.core.util.PageUtil;
import com.ynzhongxi.gpsreport.pojo.HGpsCarInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HGpsCarInfoDAO extends BaseMongoDbDao<HGpsCarInfo> {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    protected Class getEntityClass() {
        return HGpsCarInfo.class;
    }

    public List<HGpsCarInfo> getPage(HGpsCarInfo hGpsCarInfo, int page, int size) {
        int[] startEnd = PageUtil.transToStartEnd(page, size);
        Query query = super.getQueryLikeByObject(hGpsCarInfo);
        query.skip(startEnd[0]);
        query.limit(size);
        return this.mongoTemplate.find(query, this.getEntityClass());
    }

    public Map<String, Object> getHMonthCount(String month) {
        Map<String, Object> map = new HashMap<>();
        Criteria tcriteria = new Criteria();
        tcriteria.and("time").regex(".*?" + month + ".*");
        tcriteria.and("tired").is("✔");  //疲劳数据不为空
        Query tquery = new Query(tcriteria);
        long tcount = this.mongoTemplate.count(tquery, HGpsCarInfo.class);   //疲劳总量
        map.put("tcount", tcount);
        Criteria scriteria = new Criteria();
        scriteria.and("time").regex(".*?" + month + ".*");
        scriteria.and("speed").is("✔");   //超速数据不为空
        Query squery = new Query(scriteria);
        long scount = this.mongoTemplate.count(squery, HGpsCarInfo.class);   //超速总量
        map.put("scount", scount==0?" ":scount);
        Criteria ocriteria = new Criteria();
        ocriteria.and("month").regex(".*?" + month + ".*");
        ocriteria.and("online").is("是");
        Query oquery = new Query(ocriteria);
        long ocount = this.mongoTemplate.count(oquery, HGpsCarInfo.class);   //在线车辆总量
        Criteria ncriteria = new Criteria();
        ncriteria.and("month").regex(".*?" + month + ".*");
        Query nquery = new Query(ncriteria);
        long ncount = this.mongoTemplate.count(nquery, HGpsCarInfo.class);   //每月总在线数量
        map.put("icount", ncount - ocount);
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后1位
        numberFormat.setMaximumFractionDigits(1);
        if (ocount != 0) {
            String ns = numberFormat.format((float) ocount / (float) ncount * 100) + "%";   //在线率计算
            map.put("ns", ns);
        }
        if (ocount == 0) {
            map.put("ns", "0%");
        }
        map.put("name", "回通");
        map.put("month",month);
        return map;
    }
}

