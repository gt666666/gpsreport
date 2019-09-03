package com.ynzhongxi.gpsreport.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ynzhongxi.gpsreport.enums.ResultEnum;

/**
 * The type Common result.
 *
 * @author lixingwu
 */
public class CommonResult {
    public static final int SUCCESS = 0;
    public static final int FAILS = 1;
    public static final int SERVER_ERROR = -1;
    private int code;
    private String msg;
    private Object data;

    public CommonResult() {
        this.code = FAILS;
        this.msg = "";
        this.data = null;
    }

    public CommonResult(boolean success, String msg) {
        this.code = success ? SUCCESS : FAILS;
        this.msg = msg;
        this.data = null;
    }

    public CommonResult(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMessage();
        this.data = null;
    }

    public CommonResult(ResultEnum resultEnum, Object obj) {
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMessage();
        this.data = obj;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return code == SUCCESS || (code <= 1999 && code > SERVER_ERROR);
    }

    @JsonIgnore
    public boolean isFails() {
        return code == FAILS || code == SERVER_ERROR || (code >= 2000);
    }

    @Override
    public String toString() {
        return "CommonResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
