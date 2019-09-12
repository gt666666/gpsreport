package com.ynzhongxi.gpsreport.controller;

import com.ynzhongxi.gpsreport.pojo.GpsCarInfo;
import com.ynzhongxi.gpsreport.utils.JxlsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/9/11
 */

@RestController
public class GpsCarInfoContorller {
    @Autowired
    private MongoTemplate mongoTemplate;
    @GetMapping("/gpsCarInfo/getAll")
    public void exportGpsCarInfo()throws Exception{

        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("time").exists(true), Criteria.where("time").is(""));   //time不为空的所有数据
        Query query = new Query(criteria);
        List<GpsCarInfo> gpsCarInfos = this.mongoTemplate.find(query, GpsCarInfo.class);
        Map<String, Object> map = new HashMap<>();
        map.put("gpsCarInfos", gpsCarInfos);
        // 模板路径和输出流
        String templatePath = "E:\\jxls\\studentTemplate.xlsx";
        OutputStream os = new FileOutputStream("E:\\jxls\\student.xls");
        //调用封装的工具类，传入模板路径，输出流，和装有数据的Map,按照模板导出
        JxlsUtil.exportExcel(templatePath, os, map);
        os.close();
    }
}
