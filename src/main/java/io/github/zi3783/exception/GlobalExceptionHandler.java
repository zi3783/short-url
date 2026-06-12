package io.github.zi3783.exception;


import io.github.zi3783.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e){
        log.warn("参数异常:{}",e.getMessage());
        return Result.error(400,e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public Result<Void> handleException(Exception e){
        log.error("未知异常",e);
        return Result.error(500,e.getMessage());
    }

    @ExceptionHandler(value = BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e){
        log.warn("业务异常:code:{},message:{}",e.getCode(),e.getMessage());
        return Result.error(e.getCode(),e.getMessage());
    }
}
