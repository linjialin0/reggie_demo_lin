package com.itheima.reggie.exception;

import com.itheima.reggie.pojo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

//全局异常处理器
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
//只捕获controller层异常
@ResponseBody
@Slf4j
public class GlobaExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> ex(SQLIntegrityConstraintViolationException ex) {
        ex.printStackTrace();
        //捕获异常

        if (ex.getMessage().contains("Duplicate entry")) {
            String[] s = ex.getMessage().split(" ");
            String msg = s[2] + "已存在";

            return R.error(msg);
        }


        return R.error("未知错误");
        //返回标准错误信息
    }

    /**
     * 自定义异常捕获
     */

    @ExceptionHandler(CustomException.class)
    public R<String> ex(CustomException ex) {
        ex.printStackTrace();
        //捕获异常


        return R.error(ex.getMessage());
        //返回标准错误信息
    }
}
