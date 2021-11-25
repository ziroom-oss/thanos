package com.ziroom.qa.quality.defende.provider.result;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class WebResponseException extends WebAbstractResponseAdapter {

    //所有异常进行包装
    @ExceptionHandler(value = Throwable.class)
    public Object exceptionHandler(Exception e) {
        if (e instanceof CustomException) {
            CustomException exception = (CustomException) e;
            return RestResultVo.fromErrorMessage(exception.getMessage(), exception.getCode());
        }
        if(e instanceof MethodArgumentNotValidException){
            return RestResultVo.fromErrorMessage(e.getMessage(),CustomExceptionTypeEnum.PARAM_ERROR.getCode());
        }
        log.error("统一异常拦截", e);
        return RestResultVo.fromErrorMessage(e.getMessage(), CustomExceptionTypeEnum.OTHER_ERROR.getCode());
    }

}
