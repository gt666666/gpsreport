package com.ynzhongxi.gpsreport.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Validator;
import com.ynzhongxi.gpsreport.enums.ResultEnum;
import com.ynzhongxi.gpsreport.exception.FailException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 数据校验工具类
 *
 * @author lixingwu "lixingwu"
 */
public class ValidatorUtils {

    /**
     * <p>方法名称：校验参数是否为空，为空抛出FailException异常.</p>
     * <p>创建时间：2019-07-22 11:43:52</p>
     * <p>创建作者：李兴武</p>
     *
     * @param obj    校验数据对象
     * @param result 异常枚举
     * @author "lixingwu"
     */
    public static void isNull(Object obj, ResultEnum result) {
        if (Validator.isNull(obj)) {
            throw new FailException(result);
        }
    }

    /**
     * <p>方法名称：校验参数是否为空，为空抛出FailException异常.</p>
     * <p>创建时间：2019-07-23 10:39:28</p>
     * <p>创建作者：李兴武</p>
     *
     * @param obj    校验数据对象
     * @param result 异常枚举，可以使用模板
     * @param param  参数
     * @author "lixingwu"
     */
    public static void isNull(Object obj, ResultEnum result, Object... param) {
        if (Validator.isNull(obj)) {
            throw new FailException(result, param);
        }
    }

    /**
     * <p>方法名称：判断数据是否为空.</p>
     * <p>创建时间：2019-07-23 11:18:47</p>
     * <p>创建作者：李兴武</p>
     *
     * @param obj    数据对象那个
     * @param result 异常枚举
     * @param param  参数
     * @author "lixingwu"
     */
    public static void isBank(Object obj, ResultEnum result, Object... param) {
        if (obj instanceof Collection) {
            if (CollUtil.isEmpty(Convert.convert(Collection.class, obj))) {
                throw new FailException(result, param);
            }
        }
        if (Tools.isBlank(obj)) {
            throw new FailException(result, param);
        }
    }

    /**
     * <p>方法名称：判断数据是否为空.</p>
     * <p>创建时间：2019-07-23 11:33:27</p>
     * <p>创建作者：李兴武</p>
     *
     * @param obj    数据对象那个
     * @param result 异常枚举
     * @author "lixingwu"
     */
    public static void isBank(Object obj, ResultEnum result) {
        isBank(obj, result, "");
    }

    public static void main(String[] args) {
        ArrayList<Object> objects = CollUtil.newArrayList();
        isBank(objects, ResultEnum.ERROR_NO_EXIST);
    }

}
