package com.ynzhongxi.gpsreport.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 测试类
 *
 * @author lixingwu
 */
@Data
public class Test implements Serializable {
    /** MongoDb自动生成的唯一标识 */
    private String id;
    /** 登录次数，数字 */
    private Integer loginNum;
    /** 是否删除，布尔值 */
    private Boolean isDelete;
    /** 时间存时间戳，不然MongoDb不好解析 */
    private Long createTime;
    /** ip存长地址 */
    private Long ip;
}
