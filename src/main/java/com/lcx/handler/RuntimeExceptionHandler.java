package com.lcx.handler;

import com.lcx.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class RuntimeExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Result runtimeException(RuntimeException e) {
        log.error("运行时异常信息：{}", e.getMessage());
        return Result.error(e.getMessage());
    }
}
