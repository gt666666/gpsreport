package com.ynzhongxi.gpsreport.dao;

import cn.hutool.core.util.PageUtil;
import com.ynzhongxi.gpsreport.pojo.HGpsCarInfo;
import com.ynzhongxi.gpsreport.pojo.JGpsCarInfo;
import com.ynzhongxi.gpsreport.utils.DateFormatUtil;
import com.ynzhongxi.gpsreport.utils.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/9/29
 */
@Component
public class JGpsCarInfoDAO extends BaseMongoDbDao<JGpsCarInfo> {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    protected Class<JGpsCarInfo> getEntityClass() {
        return JGpsCarInfo.class;
    }

    public List<JGpsCarInfo> getPage(JGpsCarInfo jGpsCarInfo, int page, int size) {
        int[] startEnd = PageUtil.transToStartEnd(page, size);
        Query query = super.getQueryLikeByObject(jGpsCarInfo);
        query.skip(startEnd[0]);
        query.limit(size);
        return this.mongoTemplate.find(query, this.getEntityClass());
    }

    public Map<String, Object> getJMonthCount(String month) {
        Map<String, Object> map = new HashMap<>();
        Criteria tcriteria = new Criteria();
        tcriteria.and("time").regex(".*?" + month + ".*");
        tcriteria.and("tired").is("✔");  //疲劳数据不为空
        Query tquery = new Query(tcriteria);
        long tcount = this.mongoTemplate.count(tquery, JGpsCarInfo.class);   //疲劳总量
        //疲劳车辆车牌号
        List<JGpsCarInfo> tjGpsCarInfos = this.mongoTemplate.find(tquery, JGpsCarInfo.class);
        StringBuffer   tstr=new StringBuffer();
        for(int i=0;i<5;i++){
            tstr.append(tjGpsCarInfos.get(i).getCarNumber());
        }
        map.put("trCarNumebr",tstr);

        map.put("tcount", tcount);
        Criteria scriteria = new Criteria();
        scriteria.and("time").regex(".*?" + month + ".*");
        scriteria.and("speed").is("✔");   //超速数据不为空
        Query squery = new Query(scriteria);
        long scount = this.mongoTemplate.count(squery, JGpsCarInfo.class);   //超速总量
        //超速车辆车牌号
        List<JGpsCarInfo> sjGpsCarInfos = this.mongoTemplate.find(squery, JGpsCarInfo.class);
        StringBuffer   sstr=new StringBuffer();
        if(sjGpsCarInfos.size()>6){
            for(int  i=0;i<5;i++){
                sstr.append(sjGpsCarInfos.get(i).getCarNumber());
            }
        }
        if(sjGpsCarInfos.size()<6&&sjGpsCarInfos.size()>0){
            for(int i=0; i<sjGpsCarInfos.size();i++){
                sstr.append(sjGpsCarInfos.get(i).getCarNumber());
            }
        }
        map.put("spCarNumber",sstr.length()>35?sstr.append("......"):sstr);

        map.put("scount", scount);
        Criteria ocriteria = new Criteria();
        ocriteria.and("month").regex(".*?" + month + ".*");
        ocriteria.and("online").is("是");
        Query oquery = new Query(ocriteria);
        long ocount = this.mongoTemplate.count(oquery, JGpsCarInfo.class);   //在线车辆总量
        //不在线车辆车牌号
        Criteria oncriteria = new Criteria();
        oncriteria.and("month").regex(".*?" + month + ".*");
        oncriteria.and("online").is("否");
        Query onquery = new Query(oncriteria);
        List<JGpsCarInfo> jGpsCarInfos=this.mongoTemplate.find(onquery,JGpsCarInfo.class);
        StringBuffer   str=new StringBuffer();
        for(int i=0;i<5;i++){
            str.append(jGpsCarInfos.get(i).getCarNumber());
        }
        map.put("noCarNumber",str);

        Criteria ncriteria = new Criteria();
        ncriteria.and("month").regex(".*?" + month + ".*");
        Query nquery = new Query(ncriteria);
        long ncount = this.mongoTemplate.count(nquery, JGpsCarInfo.class);   //每月总在线数量
        map.put("icount", ncount - ocount);

        if (ocount != 0) {
            String ns = Tools.getNumberFormat().format((float) ocount / (float) ncount * 100) + "%";   //在线率计算
            map.put("ns", ns);
        }
        if (ocount == 0) {
            map.put("ns", "0%");
        }
        map.put("name", "锦通");
        map.put("month",  month );

        map.put("iicount", Tools.getNumberFormat().format((float)( ncount - ocount)/(float)ncount*100)+"%"); //不在线所占比率
        map.put("sscount",Tools.getNumberFormat().format((float) scount / (float) ncount * 100) + "%");   //超速10%所长比率
        map.put("ttcount",Tools.getNumberFormat().format((float) tcount / (float) ncount * 100) + "%");    //疲劳驾驶所长比率
        return map;
    }
}
