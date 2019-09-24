package com.ynzhongxi.gpsreport.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * by 罗毅涵
 * The 存储车辆基础数据
 */
@Data
public class HGpsCarInfo implements Serializable {
    /**
     * 序列号
     **/
    private Integer num;

    /**
     * 车牌号
     **/
    private String carNumber;

    /**
     * 驾驶员
     **/
    private String driverName;
    /**
     * 驾驶员手机号
     **/
    private String phone;
    /**
     * 是否在线
     **/
    private String online;
    /**
     * 最后在线时间
     **/
    private String time;
    /**
     * 行驶速度
     **/
    private Double sp;
    /**
     * 当前位置
     **/
    private String pos;
    /**
     * 车台故障,GPS在线状态   1：车台故障  0：没有故障
     **/
    private String type;
    /**
     * 疲劳驾驶 1:表示疲劳驾驶
     **/
    private String tired;
    /**
     * 超速 1：表示超速
     **/
    private String speed;

    /**
     * 是否有报警数据  ：有、无
     **/
    private String data;

    /**
     * 车辆设备号
     **/
    private String deviceId;
    /**
     * 具体处理方式
     **/
    private String processMode;
}
