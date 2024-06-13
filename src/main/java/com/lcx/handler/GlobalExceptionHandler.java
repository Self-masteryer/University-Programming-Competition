package com.lcx.handler;

import com.lcx.common.constant.ErrorMessage;
import com.lcx.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception ex, WebRequest request) {
        Map<String ,Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("timeStamp", Instant.now());
        errorDetails.put("status", HttpStatus.INTERNAL_SERVER_ERROR);// 服务错误
        errorDetails.put("error", "internal server error");
        errorDetails.put("message", ex.getMessage());

        log.error("全局异常信息：{}", ex.getMessage());
        return Result.error(errorDetails,"internal server error");
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        String msg = ex.getMessage();
        if (msg.contains("Duplicate entry")) {
            String[] s = msg.split(" ");
            msg = s[2] + ErrorMessage.ALREADY_EXISTS;
            return Result.error(msg);
        } else {
            return Result.error(StringUtils.hasLength(msg) ? msg : "操作失败");
        }
    }

}