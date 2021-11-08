package com.ziroom.qa.quality.defende.provider.result;

/**
 * 自定义异常有两个核心内容，一个是code。使用CustomExceptionType 来限定范围。
 * 另外一个是message，这个message信息是要最后返回给前端的，所以需要用友好的提示来表达异常发生的原因或内容
 *
 */
public class CustomException extends RuntimeException {
    //异常错误编码
    private int code;
    //异常信息
    private String message;

    //无参构造函数
    private CustomException() {
    }

    //使用分类固化异常，不希望程序员创建异常 ，===使用CustomExceptionType 来限定范围
    public CustomException(CustomExceptionTypeEnum exceptionTypeEnum) {
        this.code = exceptionTypeEnum.getCode();
        this.message = exceptionTypeEnum.getDesc();
    }
    //这个message信息是要最后返回给前端的，所以需要用友好的提示来表达异常发生的原因或内容
    public CustomException(CustomExceptionTypeEnum exceptionTypeEnum,
                           String message) {
        this.code = exceptionTypeEnum.getCode();
        this.message = message;
    }
    public CustomException(String message) {
        this.code = CustomExceptionTypeEnum.USER_INPUT_ERROR.getCode();
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
