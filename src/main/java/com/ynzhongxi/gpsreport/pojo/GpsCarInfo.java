package com.ynzhongxi.gpsreport.pojo;

import lombok.Data;

@Data
public class GpsCarInfo {
    /** MongoDb自动生成的唯一标识 */
    private String id;
    /**车牌号**/
    private String carNumber;
    /**车辆设备号**/
    private String deviceId;
    /**驾驶员**/
    private String driverName;
}
