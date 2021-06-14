package com.atguigu.gulimall.product.exception;

import com.atguigu.common.utils.R;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.atguigu.gulimall.product.controller")
public class ExceptionController {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    private R hanleValidException(MethodArgumentNotValidException e) {
        Map<String, String> map = new HashMap<>();
        BindingResult result = e.getBindingResult();
        result.getFieldErrors().forEach((item) -> {
            String message = item.getDefaultMessage();
            String field = item.getField();
            map.put(field, message);
        });
        return R.error(400,"数据校验异常").put("data",map);
    }
    @ExceptionHandler(value = Throwable.class)
    private R hanleException(Throwable throwable) {
        return R.error();
    }
}
