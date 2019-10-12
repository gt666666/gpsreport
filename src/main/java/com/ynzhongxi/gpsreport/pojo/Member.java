package com.ynzhongxi.gpsreport.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/10/10
 */
@Data
public class Member implements Serializable {
    private String userName;
    private String password;
}
