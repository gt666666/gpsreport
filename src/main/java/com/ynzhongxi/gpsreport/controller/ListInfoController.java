package com.ynzhongxi.gpsreport.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/10/18
 */
@RestController
public class ListInfoController {

    @RequestMapping(value = "/getListController")
    public List<String> getListController(){
        File   file=new File("D:"+File.separator+"WebStorm"+File.separator+"gps_cms"+File.separator+"doc");
        List<String> list=new ArrayList<>();
        File[] files = file.listFiles();
        for (File f:files){
            String str= f.toString();
            String substring = str.substring(str.indexOf("\\doc"));
            list.add(substring);
        }
        return   list;
    }
}
