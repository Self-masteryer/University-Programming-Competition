package com.lcx.handler;

import com.lcx.common.constant.ErrorMessageConstant;
import com.lcx.common.exception.BaseException;
import com.lcx.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    public Result exceptionHandler(BaseException ex) {
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        String msg = ex.getMessage();
        if (msg.contains("Duplicate entry")) {
            String[] s = msg.split(" ");
            msg = s[2] + ErrorMessageConstant.ALREADY_EXISTS;
            return Result.error(msg);
        } else {
            return Result.error(StringUtils.hasLength(msg) ? msg : "操作失败");
        }
    }
}
