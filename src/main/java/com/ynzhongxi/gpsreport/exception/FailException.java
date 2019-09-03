package com.ynzhongxi.gpsreport.exception;

import cn.hutool.core.text.StrFormatter;
import com.ynzhongxi.gpsreport.enums.ResultEnum;

/**
 * 自定义异常类，用于返回service操作失败，返回失败信息
 * 自定义异常会被全局异常拦截，并返回json数据
 *
 * @author lixingwu
 */
public class FailException extends RuntimeException {

    private Integer code;
    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public FailException() {
        super();
    }

    public FailException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }

    public FailException(ResultEnum resultEnum, Object... param) {
        super(StrFormatter.format(resultEnum.getMessage(), param));
        this.code = resultEnum.getCode();
    }

    public FailException(ResultEnum resultEnum, Object data) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
        this.data = data;
    }
}
