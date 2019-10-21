package com.ynzhongxi.gpsreport.pojo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/10/18
 */
public class Demo {
    public static void main(String [] args){
        File   file=new File("D:"+File.separator+"WebStorm"+File.separator+"gps_cms"+File.separator+"doc");
        List<String>  list=new ArrayList<>();
        File[] files = file.listFiles();
        for (File f:files){
            String str= f.toString();
            String substring = str.substring(str.indexOf("\\doc"));
            list.add(substring);
        }
        System.out.println(list);
    }
}
