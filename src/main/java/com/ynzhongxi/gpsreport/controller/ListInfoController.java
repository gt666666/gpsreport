package com.ynzhongxi.gpsreport.controller;

import com.ynzhongxi.gpsreport.component.ConfigProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/10/18
 */
@RestController
public class ListInfoController {
    private final ConfigProperty property;

    @Autowired
    public ListInfoController(ConfigProperty property) {
        this.property = property;
    }

    @RequestMapping(value = "/getListController")
    public List<String> getListController() {
        File file = new File(property.getGpsCmsPath() + "\\doc");
        List<String> list = new ArrayList<>();
        File[] files = file.listFiles();
        for (File f : Objects.requireNonNull(files)) {
            String str = f.toString();
            String substring = str.substring(str.indexOf("\\doc"));
            list.add(substring);
        }
        return list;
    }
}
