package com.ziroom.qa.quality.defende.provider.result;

/**
 * ExceptionTypeEnum 枚举异常分类，将异常分类固化下来，防止开发人员思维发散。
 */
public enum CustomExceptionTypeEnum {
    SUCCESS(200, "成功！"),
    USER_INPUT_ERROR(400, "您输入的数据错误或您没有权限访问资源！"),
    SYSTEM_ERROR(500, "系统出现异常，请您稍后再试或联系管理员！"),
    PARAM_ERROR(600, "参数异常！"),
    OTHER_ERROR(999, "系统出现未知异常，请联系管理员！"),
    USERNAME_NOMATCH(5001, "登陆信息跟当前用户username不匹配！");
    private String desc;//异常类型中文描述

    private int code; //code

    //构造方法
    CustomExceptionTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    //get set方法
    public String getDesc() {
        return desc;
    }

    public int getCode() {
        return code;
    }
}
