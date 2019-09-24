package com.ynzhongxi.gpsreport.pojo;

import lombok.Data;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/9/23
 */
@Data
public class JGpsCarDetails {
    /**
     * 序号
     **/
    private int num;
    /**
     * 车牌号
     **/
    private String carNumber;
    /**
     * 司机名字
     **/
    private String carName;
    /**
     * 报警类型
     **/
    private String type;
    /**
     * 报警时间
     **/
    private String time;
    /**
     * 报警地点
     **/
    private String sps;
    /**
     * 车速
     **/
    private Double speed;
    /**
     * 处理方式
     **/
    private String way;
    /**
     * 回执状态
     **/
    private String status;
    /**
     * 处理时间
     **/
    private String wayTime;
    /**
     * 备注
     **/
    private String note;
}
