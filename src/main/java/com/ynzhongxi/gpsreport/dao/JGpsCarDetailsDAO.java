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
public class JGpsCarDetailsDAO extends  BaseMongoDbDao<JGpsCarDetails> {
    @Autowired
    private MongoTemplate  mongoTemplate;
    @Override
    protected Class<JGpsCarDetails> getEntityClass() {
        return JGpsCarDetails.class;
    }
    public List<JGpsCarDetails> getPage(String  time, int page, int size) {
//        int[] startEnd = PageUtil.transToStartEnd(page, size);
//        Query query = super.getQueryLikeByObject(jGpsCarDetails);
//        query.skip(startEnd[0]);
//        query.limit(size);
        Query query = new Query();
        Criteria criteria = Criteria.where("time").regex(".*?" + time + ".*");
        query.addCriteria(criteria);
      return this.mongoTemplate.find(query, this.getEntityClass());
    }
    public Long getLikeCount(String  time) {
        Query query = new Query();
        Criteria criteria = Criteria.where("time").regex(".*?" + time + ".*");
        query.addCriteria(criteria);
        return     this.mongoTemplate.count(query,HGpsCarDetails.class);
    }
    public List<JGpsCarDetails> getPageDetails(int page, int size) {
        Query query = new Query();
        query.skip(page);
        query.limit(size);
        return this.mongoTemplate.find(query, this.getEntityClass());
    }
    public Long getDetalisLikeCount() {
        long count = this.mongoTemplate.count(new Query(new Criteria().orOperator(Criteria.where("_id").exists(true))), HGpsCarDetails.class);
        return  count;
    }
}
