package com.ynzhongxi.gpsreport.dao;

import cn.hutool.core.util.PageUtil;
import com.ynzhongxi.gpsreport.pojo.HGpsCarDetails;
import com.ynzhongxi.gpsreport.pojo.JGpsCarDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/9/29
 */
@Component
public class JGpsCarDetailsDAO extends BaseMongoDbDao<JGpsCarDetails> {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    protected Class<JGpsCarDetails> getEntityClass() {
        return JGpsCarDetails.class;
    }

    public List<JGpsCarDetails> getPage(String time,String type, int page, int pageSize) {
        int[] startEnd = PageUtil.transToStartEnd(page, pageSize);
//        Query query = super.getQueryLikeByObject(jGpsCarDetails);
        Query query = new Query();
        query.skip(startEnd[0]);
        query.limit(pageSize);
        Criteria criteria = Criteria.where("time").regex(".*?" + time + ".*").and("type").regex(".*?" + type + ".*");
        query.addCriteria(criteria);
        return this.mongoTemplate.find(query, this.getEntityClass());
    }

    public Long getLikeCount(String time,String type) {
        Query query = new Query();
        Criteria criteria = Criteria.where("time").regex(".*?" + time + ".*").and("type").regex(".*?" + type + ".*");
        query.addCriteria(criteria);
        return this.mongoTemplate.count(query, this.getEntityClass());
    }

    public List<JGpsCarDetails> getPageDetails(int page, int pageSize) {
        int[] startEnd = PageUtil.transToStartEnd(page, pageSize);
//        Query query = super.getQueryLikeByObject(hGpsCarDetails);
        Query query = new Query();
        query.skip(startEnd[0]);
        query.limit(pageSize);
        return this.mongoTemplate.find(query, this.getEntityClass());
    }

    public Long getDetalisLikeCount() {
        long count = this.mongoTemplate.count(new Query(new Criteria().orOperator(Criteria.where("_id").exists(true))), this.getEntityClass());
        return count;
    }
}
