package com.ynzhongxi.gpsreport.controller;

import com.ynzhongxi.gpsreport.enums.ResultEnum;
import com.ynzhongxi.gpsreport.pojo.CommonResult;
import com.ynzhongxi.gpsreport.pojo.HCarInfo;
import com.ynzhongxi.gpsreport.pojo.Page;
import com.ynzhongxi.gpsreport.service.HCarInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/9/29
 */
@RestController
@RequestMapping(value = "/hInfo")
public class HCarInfoControler extends BaseController {
    @Autowired
    private HCarInfoService hCarInfoService;


    /**
     * <p>方法名称：Edit by car number common result.</p >
     * <p>详细描述：. 修改司机信息</p >
     * <p>创建时间：2019-09-30 10:01:29</p >
     * <p>创建作者：高挺</p >
     * <p>修改记录：</p >
     *
     * @param hCarInfo the h car info
     * @return the common result
     * @author "gaoting"
     */
    @PostMapping("/editByCarNumber")
    public CommonResult editByCarNumber(HCarInfo hCarInfo) {
        this.hCarInfoService.editByCarNumber(hCarInfo);
        return resultWrapper(ResultEnum.SUCCESS);
    }

    /**
     * <p>方法名称：List car info page.</p >
     * <p>详细描述：.分页查询司机信息</p >
     * <p>创建时间：2019-09-30 10:01:58</p >
     * <p>创建作者：高挺</p >
     * <p>修改记录：</p >
     *
     * @param hCarInfo the h car info
     * @param page     the page
     * @param pageSize the page size
     * @return the page
     * @author "gaoting"
     */
    @GetMapping("/carInfoList")
    public Page<HCarInfo> listCarInfo(HCarInfo hCarInfo,
                                      @RequestParam(name = "page",required = false,defaultValue = "1") int page,
                                      @RequestParam(name="limit" , required=false  ,defaultValue="10") int pageSize) {
        return this.hCarInfoService.listCarInfo(hCarInfo, page, pageSize);
    }

    /**
     * <p>方法名称：Car info by carnumber h car info.</p >
     * <p>详细描述：.根据Id查询司机信息</p >
     * <p>创建时间：2019-09-30 10:02:01</p >
     * <p>创建作者：高挺</p >
     * <p>修改记录：</p >
     *
     * @param id the h car info
     * @return the h car info
     * @author "gaoting"
     */
    @GetMapping("/carInfoById")
    public HCarInfo carInfoById(String  id) {
        return this.hCarInfoService.carInfoById(id);
    }

    /**
     * <p>方法名称：Delete by carnumber common result.</p >
     * <p>详细描述：.根据</p >
     * <p>创建时间：2019-09-30 10:02:05</p >
     * <p>创建作者：高挺</p >
     * <p>修改记录：</p >
     *
     * @param hCarInfo the h car info
     * @return the common result
     * @author "gaoting"
     */
    @PostMapping("/deleteByCarnumber")
    public CommonResult deleteByCarnumber(HCarInfo hCarInfo) {
        int i = this.hCarInfoService.deleteByCarnumber(hCarInfo);
        if (i == 1) {
            return resultWrapper(ResultEnum.SUCCESS);
        }
        return resultWrapper(ResultEnum.ERROR);
    }

    /**
     * <p>方法名称：Insert h car info common result.</p >
     * <p>详细描述：.</p >
     * <p>创建时间：2019-09-30 10:02:07</p >
     * <p>创建作者：高挺</p >
     * <p>修改记录：</p >
     *
     * @param hCarInfo the h car info
     * @return the common result
     * @author "gaoting"
     */
    @PostMapping("insertHCarInfo")
    public  CommonResult   insertHCarInfo(HCarInfo  hCarInfo){
          this.hCarInfoService.insertHCarInfo(hCarInfo);
        return resultWrapper(ResultEnum.SUCCESS);
    }

}
