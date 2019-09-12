package com.ynzhongxi.gpsreport.controller;

import com.ynzhongxi.gpsreport.pojo.Course;
import com.ynzhongxi.gpsreport.pojo.GpsCarInfo;
import com.ynzhongxi.gpsreport.pojo.Student;
import com.ynzhongxi.gpsreport.utils.JxlsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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
    @GetMapping("/getStudent")
    public  void  getStudent(){
        List<Student> students = new ArrayList<>();
        Student student = new Student();
        student.setName("张三");
        student.setGender("男");
        student.setGradeClass("初一一班");
        List<Course> courses = new ArrayList<>();
        Course course = new Course();
        course.setCourseName("语文");
        course.setCourseScore("98");
        courses.add(course);
        course = new Course();
        course.setCourseName("数学");
        course.setCourseScore("105");
        courses.add(course);
        course = new Course();
        course.setCourseName("物理");
        course.setCourseScore("80");
        courses.add(course);

        student.setCourses(courses);
        students.add(student);


        student = new Student();
        student.setName("王丽丽");
        student.setGender("女");
        student.setGradeClass("初一二班");
        courses = new ArrayList<>();
        course = new Course();
        course.setCourseName("语文");
        course.setCourseScore("102");
        courses.add(course);
        course = new Course();
        course.setCourseName("数学");
        course.setCourseScore("110");
        courses.add(course);
        student.setCourses(courses);
        students.add(student);

        student = new Student();
        student.setName("李梅");
        student.setGender("女");
        student.setGradeClass("初一三班");
        courses = new ArrayList<>();
        course = new Course();
        course.setCourseName("语文");
        course.setCourseScore("110");
        courses.add(course);
        course = new Course();
        course.setCourseName("数学");
        course.setCourseScore("100");
        courses.add(course);
        course = new Course();
        course.setCourseName("物理");
        course.setCourseScore("85");
        courses.add(course);
        student.setCourses(courses);
        students.add(student);
        //模板里展示的数据
        Map<String, Object> data = new HashMap<>();
        data.put("students", students);
        data.put("AAAA","握手");
        System.out.println(data.get("students"));
        // 模板路径和输出流
        String templatePath = "E:\\jxls\\studentTemplate.xls";
        OutputStream os = null;
        try {
            os = new FileOutputStream("E:\\jxls\\student.xls");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //调用封装的工具类，传入模板路径，输出流，和装有数据的Map,按照模板导出
        try {
            JxlsUtil.exportExcel(templatePath, os, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
