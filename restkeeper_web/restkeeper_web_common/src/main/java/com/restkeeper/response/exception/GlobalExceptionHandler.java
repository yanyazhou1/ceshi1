package com.restkeeper.response.exception;


import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Objects;

/**
 * 全局的异常处理类
 *   用来处理Controller发生的所有异常
 */
@RestControllerAdvice(basePackages = "com.restkeeper")
public class GlobalExceptionHandler {

    /**
     * 要拦截的异常类型
     */
    @ExceptionHandler(Exception.class)
    public Object Exception(Exception e){
       HashMap<String, Object> errorMap = new HashMap<>();
       errorMap.put("errorCode","000000");
       errorMap.put("errorMsg","后端发生异常:"+e.getMessage());
       return errorMap;
    }
}
