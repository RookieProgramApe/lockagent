package com.lxkj.common.exception;


import com.lxkj.common.bean.JsonResults;
import com.lxkj.common.bean.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 统一异常处理
 */
@ControllerAdvice
@RestController
@Slf4j
public class GlobalExceptionHandler {

    /*通用异常*/
    @ExceptionHandler(Exception.class)
    public JsonResults handlerException(HttpServletRequest req, Exception e) {
        log.error(e.getMessage(),e);
        return new JsonResults(this.getStatus(req).value(),e.getMessage());
    }
    /*空指针异常*/
    @ExceptionHandler(NullPointerException.class)
    public JsonResults NullPointerException(NullPointerException e) {
        log.error("空指针异常",e);
        return new JsonResults(ResultCodeEnum.FAIL.getCode(),"空指针异常");
    }

    /*自定义异常*/
    @ExceptionHandler(BusinessException.class)
    public JsonResults ParamJsonException(BusinessException e) {
        log.warn(e.getMessage());
        return new JsonResults(ResultCodeEnum.FAIL.getCode(),e.getMessage());
    }

    /* @Valid 参数验证异常 Hibernate-Validation */
    @ExceptionHandler(BindException.class)
    public JsonResults bindExceptionHandler(BindException e){
        log.warn(e.getBindingResult().getFieldError().getDefaultMessage());
        return new JsonResults(ResultCodeEnum.FAIL.getCode(),e.getBindingResult().getFieldError().getDefaultMessage());
    }

    /* Token验证失败 */
    @ExceptionHandler(TokenException.class)
    public JsonResults TokenException(TokenException e){
        log.warn(e.getMessage());
        return new JsonResults(ResultCodeEnum.TOKENN_OTFOUND.getCode(),e.getMessage());
    }

    /* Shiro 无权限抛出异常 */
//    @ExceptionHandler(UnauthorizedException.class)
//    public JsonResults UnauthorizedException(UnauthorizedException e){
//        log.warn(e.getMessage());
//        return new JsonResults(ResultCodeEnum.FAIL.getCode(),"您没有该权限,请联系管理员!");
//    }

    /**
     * 获取错误编码
     * @param request
     * @return
     * @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
     */
    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }


}
