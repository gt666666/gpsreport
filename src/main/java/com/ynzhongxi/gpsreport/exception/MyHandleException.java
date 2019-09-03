package com.ynzhongxi.gpsreport.exception;

import com.ynzhongxi.gpsreport.pojo.CommonResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 全局异常处理器
 *
 * @author lixingwu
 */
@ControllerAdvice
public class MyHandleException {

    /**
     * 参数异常拦截
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public CommonResult missingParamterHandler(MissingServletRequestParameterException e) {
        String paramsName = e.getParameterName();
        CommonResult commonResult = new CommonResult();
        commonResult.setCode(CommonResult.SERVER_ERROR);
        commonResult.setMsg(String.format("参数不存在：%s", paramsName));
        e.printStackTrace();
        return commonResult;
    }

    /**
     * 自定义异常类FailException
     */
    @ExceptionHandler(FailException.class)
    @ResponseBody
    public CommonResult failException(FailException e) {
        e.printStackTrace();
        CommonResult commonResult = new CommonResult();
        commonResult.setCode(e.getCode());
        commonResult.setData(e.getData());
        commonResult.setMsg(e.getMessage());
        return commonResult;
    }

    /**
     * 异常信息转化为CommonResult对象，格式化输出
     *
     * @param e 移除对象
     * @return commonResult
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonResult handleException(Exception e) {
        e.printStackTrace();
        CommonResult commonResult = new CommonResult();
        commonResult.setCode(CommonResult.SERVER_ERROR);
        commonResult.setMsg(e.getMessage());
        return commonResult;
    }
}
