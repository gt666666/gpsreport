package com.ynzhongxi.gpsreport.controller;

import com.ynzhongxi.gpsreport.enums.ResultEnum;
import com.ynzhongxi.gpsreport.pojo.CommonResult;
import org.springframework.stereotype.Controller;

/**
 * Controller 基类
 *
 * @author lixingwu
 */
@Controller
public class BaseController {

    /**
     * 方法描述：自定义状态码的CommonResult
     * 创建作者：李兴武
     * 创建日期：2018-08-15
     *
     * @param code 自定义代码
     * @param msg  提示消息
     * @param obj  数据对象
     * @return CommonResult
     */
    protected CommonResult resultWrapper(int code, String msg, Object obj) {
        CommonResult result = new CommonResult();
        result.setMsg(msg);
        result.setData(obj);
        result.setCode(code);
        return result;
    }

    /**
     * 方法描述：自定义状态码的CommonResult.
     * 创建时间：2019-06-11 23:56:45
     * 创建作者：李兴武
     *
     * @param resultEnum 状态枚举
     * @return the common result
     * @author "lixingwu"
     */
    protected CommonResult resultWrapper(ResultEnum resultEnum) {
        CommonResult result = new CommonResult();
        result.setMsg(resultEnum.getMessage());
        result.setCode(resultEnum.getCode());
        return result;
    }

    /**
     * 方法描述：自定义状态码的CommonResult.
     * 创建时间：2019-06-11 23:56:45
     * 创建作者：李兴武
     *
     * @param resultEnum 状态枚举
     * @param object     传递数据
     * @author "lixingwu"
     */
    protected CommonResult resultWrapper(ResultEnum resultEnum, Object object) {
        CommonResult result = new CommonResult();
        result.setMsg(resultEnum.getMessage());
        result.setCode(resultEnum.getCode());
        result.setData(object);
        return result;
    }


    /**
     * <p> 方法描述：bool 判断返回不同的枚举信息. </p>
     * <p> 创建时间：2018-10-15 18:20:11 </p>
     * <p> 创建作者：刘万琼 </p>
     *
     * @param bool        状态
     * @param successEnum the success enum
     * @param failsEnum   the fails enum
     * @param obj         数据对象
     * @return the common result
     */
    protected CommonResult resultWrapper(boolean bool, ResultEnum successEnum, ResultEnum failsEnum, Object obj) {
        return resultWrapper(bool ? successEnum : failsEnum, obj);
    }

    /**
     * <p> 方法描述：bool 判断返回不同的枚举信息. </p>
     * <p> 创建时间：2018-10-15 18:20:11 </p>
     * <p> 创建作者：刘万琼 </p>
     *
     * @param bool        状态
     * @param successEnum the success enum
     * @param failsEnum   the fails enum
     * @return the common result
     */
    protected CommonResult resultWrapper(boolean bool, ResultEnum successEnum, ResultEnum failsEnum) {
        return resultWrapper(bool ? successEnum : failsEnum);
    }
}
