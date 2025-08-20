package com.neutech.config;

import com.neutech.vo.ResultCode;
import com.neutech.vo.ResultJson;
import org.apache.ibatis.jdbc.Null;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DefaultException {
    @ExceptionHandler(NeutechException.class)
    ResultJson defaultNeutechExceptionHandler(Exception ex) {
        ex.printStackTrace();
        return ResultJson.failed(ex.getMessage());
    }
    @ExceptionHandler
    ResultJson defaultExceptionHandler(Exception ex) {
        ex.printStackTrace();
        return ResultJson.failed("系统异常");
    }
}
