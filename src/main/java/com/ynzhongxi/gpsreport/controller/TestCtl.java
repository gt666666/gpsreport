package com.ynzhongxi.gpsreport.controller;

import com.ynzhongxi.gpsreport.component.ConfigProperty;
import com.ynzhongxi.gpsreport.enums.ResultEnum;
import com.ynzhongxi.gpsreport.pojo.CommonResult;
import com.ynzhongxi.gpsreport.pojo.Page;
import com.ynzhongxi.gpsreport.pojo.Test;
import com.ynzhongxi.gpsreport.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试控制层
 *
 * @author lixingwu
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestCtl extends BaseController {
    private final ConfigProperty property;
    private final TestService testService;

    @Autowired
    public TestCtl(ConfigProperty property, TestService testService) {
        this.property = property;
        this.testService = testService;
    }

    /**
     * 获取系统名称
     */
    @GetMapping("/webName")
    public CommonResult webName() {
        return resultWrapper(ResultEnum.SUCCESS, property);
    }

    /**
     * 保存记录
     */
    @PostMapping("/save")
    public CommonResult save(Test record) {
        testService.save(record);
        return resultWrapper(ResultEnum.SUCCESS);
    }

    /**
     * 根据id查询记录
     */
    @GetMapping("/get")
    public CommonResult queryById(
            @RequestParam(name = "id") String id
    ) {
        Test test = testService.queryById(id);
        return resultWrapper(ResultEnum.SUCCESS, test);
    }

    /**
     * 根据条件查询list
     */
    @GetMapping("/list")
    public CommonResult queryList(Test record) {
        List<Test> test = testService.queryList(record);
        return resultWrapper(ResultEnum.SUCCESS, test);
    }

    /**
     * 根据条件查询，如果根据条件查询出多条，只获取第一条记录.
     */
    @GetMapping("/find")
    public CommonResult queryOne(Test record) {
        Test test = testService.queryOne(record);
        return resultWrapper(ResultEnum.SUCCESS, test);
    }

    /**
     * 查询分页数据.
     */
    @GetMapping("/page")
    public Page<Test> getPage(
            Test record,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "limit", required = false, defaultValue = "15") int limit
    ) {
        return testService.getPage(record, page, limit);
    }

    /**
     * 删除对象record.
     */
    @PostMapping("/delete")
    public CommonResult delete(Test record) {
        testService.delete(record);
        return resultWrapper(ResultEnum.SUCCESS);
    }

    /**
     * 根据id删除记录.
     */
    @PostMapping("/deleteById")
    public CommonResult deleteById(
            @RequestParam(name = "id") String id
    ) {
        testService.deleteById(id);
        return resultWrapper(ResultEnum.SUCCESS);
    }
}
