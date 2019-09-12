package com.ynzhongxi.gpsreport.pojo;

import lombok.Data;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/9/10
 */
@Data
public class CarInfo {
    /** MongoDb自动生成的唯一标识 */
    private String id;
    /**车牌号**/
    private String carNumber;
    /**车辆设备号**/
    private String deviceId;
    /**驾驶员**/
    private String driverName;
    /**驾驶员手机号**/
    private String phone;
}
