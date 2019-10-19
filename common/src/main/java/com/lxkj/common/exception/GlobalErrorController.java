package com.lxkj.common.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Web 页面404 500代码处理
 */
@Controller
public class GlobalErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        return "error/error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

}
