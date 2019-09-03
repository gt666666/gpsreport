package com.ynzhongxi.gpsreport.enums;

import lombok.Getter;

/**
 * 端口返回信息枚举
 * 小于 1999 为成功信息
 * 大于 2999 为失败信息
 * <p>
 *
 * @author lixingwu
 */
@Getter
public enum ResultEnum {
    /******* 0 ~ 1999 为成功信息，以 SUCCESS_ 开头******/
    SUCCESS(1000, "成功"),
    SUCCESS_LOAD(1001, "加载成功"),
    SUCCESS_INSERT(1002, "添加成功"),
    SUCCESS_SAVE(1003, "保存成功"),
    SUCCESS_MODIFY(1004, "修改成功"),
    SUCCESS_DELETE(1005, "删除成功"),
    SUCCESS_ACTION(1006, "操作成功"),

    /******* 2000 ~ 4999 为失败信息，以 ERROR_ 开头 ******/
    ERROR(2000, "失败"),
    ERROR_LOAD(2001, "加载失败"),
    ERROR_INSERT(2002, "添加失败"),
    ERROR_SAVE(2003, "保存失败"),
    ERROR_MODIFY(2004, "修改失败"),
    ERROR_DELETE(2005, "删除失败"),
    ERROR_ACTION(2006, "操作失败"),
    ERROR_DELETE_EXIST_SUBSET(2007, "删除失败，存在子集"),
    ERROR_NO_EXIST(2008, "记录不存在"),
    ERROR_NO_ALREADY(2009, "记录已存在"),
    ERROR_LOCK_PWD(2010, "锁定密码错误"),

    /******* 5000 ~ 9999 为验证类信息，以 VERIFY_ 开头 ******/
    VERIFY_ID_EMPTY(5000, "记录id不能为空"),
    VERIFY_DATA_EMPTY(5001, "【{}】不能为空"),

    /***** 旧记录保留 *******/
    UNAUTHORIZED_UNDEFINED(3002, "权限认证异常"),
    ENUM_UNDEFINED(3003, "枚举不存在"),
    SEND_MSM_FAIL(3004, "短信发送失败"),
    VALIDATION_MOBILE_FAIL(3005, "手机号验证失败"),

    /***** 授权失败信息 ****/
    AUTH_REDIRECT_URI_DISACCORD(6000, "redirect_uri域名与后台配置不一致"),
    AUTH_REDIRECT_URI_NOT_EMPTY(6001, "redirect_uri不能为空"),
    AUTH_APP_ID_NOT_EMPTY(6002, "appid不能为空"),
    AUTH_APP_ID_UNDEFINITION(6003, "appid所属的应用不存在"),
    AUTH_APP_NAME_ALREADY_USE(6004, "应用名称已被占用"),
    ;

    private Integer code;
    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }


    /**
     * 方法描述：根据枚举的code得到这个枚举对象.
     * 创建时间：2019-06-13 16:53:52
     * 创建作者：李兴武
     *
     * @param code the code
     * @return the result enum
     * @author "lixingwu"
     */
    public static ResultEnum codeToEnum(int code) {
        for (ResultEnum resultEnum : ResultEnum.values()) {
            if (code == resultEnum.getCode()) {
                return resultEnum;
            }
        }
        return null;
    }

}
