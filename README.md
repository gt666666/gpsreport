#### 北斗主动安全云平台-报表开发
##### 一、开发环境
```text
主框架：spring boot 2.1.7 
数据库：mongodb
服务器：jetty
```
##### 二、启动方式
```text
运行com.ynzhongxi.gpsreport.GpsreportApplication即可。
注意：运行端口为8086，占用请自行到application.yml进行修改
```

##### 三、打包方式
```http
参考：https://www.cnblogs.com/lixingwu/p/11451052.html
```

##### 四、测试
请求接口：http://127.0.0.1:8086/test/webName
返回结果：
```json
{
    "code": 1000,
    "msg": "成功",
    "data": {
        "webName": "北斗主动安全云平台-报表"
    },
    "success": true
}
```
更多测试请看 com.ynzhongxi.gpsreport.controller.TestCtl 