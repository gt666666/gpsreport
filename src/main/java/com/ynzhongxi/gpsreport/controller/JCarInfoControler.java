package com.ynzhongxi.gpsreport.controller;

import com.ynzhongxi.gpsreport.enums.ResultEnum;
import com.ynzhongxi.gpsreport.pojo.CommonResult;
import com.ynzhongxi.gpsreport.pojo.JCarInfo;
import com.ynzhongxi.gpsreport.pojo.Page;
import com.ynzhongxi.gpsreport.service.JCarInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/9/29
 */
@RestController
@RequestMapping("/jInfo")
public class JCarInfoControler extends BaseController {
    @Autowired
    private JCarInfoService jCarInfoService;

    @PostMapping("/editByCarNumber")
    public CommonResult editByCarNumber(JCarInfo jCarInfo) {
        this.jCarInfoService.editByCarNumber(jCarInfo);
        return resultWrapper(ResultEnum.SUCCESS);
    }

    @GetMapping("/carInfoList")
    public Page<JCarInfo> listCarInfo(JCarInfo jCarInfo, @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                      @RequestParam(name = "limit", required = false, defaultValue = "10") int pageSize) {
        return this.jCarInfoService.listCarInfo(jCarInfo, page, pageSize);
    }

    @GetMapping("/carInfoById")
    public JCarInfo carInfoById(String id) {
        return this.jCarInfoService.carInfoById(id);
    }

    @PostMapping("/deleteByCarnumber")
    public CommonResult deleteByCarnumber(JCarInfo jCarInfo) {
        int i = this.jCarInfoService.deleteByCarnumber(jCarInfo);
        if (i == 1) {
            return resultWrapper(ResultEnum.SUCCESS);
        }
        return resultWrapper(ResultEnum.ERROR);
    }

    @PostMapping("insertJCarInfo")
    public CommonResult insertJCarInfo(JCarInfo jCarInfo) {
        this.jCarInfoService.insertJCarInfo(jCarInfo);
        return super.resultWrapper(ResultEnum.SUCCESS);
    }
}
