package com.lxkj.common.bean;


import com.lxkj.common.util.PageData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Huxiao
 */
public class BaseController {
    //仅仅作为测试使用
    protected HttpServletRequest request;
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    protected NamedParameterJdbcTemplate namedJdbcTemplate;

    /**
     * new PageData对象
     *
     * @return
     */
    public PageData getPageData() {
        return new PageData(this.getRequest());
    }

    /**
     * 得到ModelAndView
     *
     * @return
     */
    public ModelAndView BuildModelView(String url) {
        ModelAndView model= new ModelAndView();
        model.setViewName(url);
        return model;
    }

    /**
     * 得到request对象
     *
     * @return
     */
    public HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request;
    }

    /**
     * 获取登录用户的IP
     *
     * @param
     */
    public String getRemortIP() {
        HttpServletRequest request = this.getRequest();
        String ip = "";
        if (request.getHeader("x-forwarded-for") == null) {
            ip = request.getRemoteAddr();
        } else {
            ip = request.getHeader("x-forwarded-for");
        }
        return ip;
    }

    /**
     * 获取token值
     *
     * @return
     */
    protected String getToken() {
        String token = this.getRequest().getHeader("token");
        return token;
    }
    /**
     * 获取客户端用户
     *
     * @return
     */
    protected Object getMember() {
        Object member =this.getRequest().getAttribute("Member");
        return member;
    }


    /**
     * josn返回
     */
    //失败 code=400
    protected <T> JsonResults<T> BuildFailJson(String msg) {
        JsonResults bean = new JsonResults();
        bean.setCode(ResultCodeEnum.FAIL.getCode());//500错误
        bean.setMsg(msg);
        return bean;
    }

    //成功
    protected JsonResults BuildSuccessJson() {
        JsonResults bean = new JsonResults();
        bean.setCode(ResultCodeEnum.SUCCESS.getCode());
        return bean;
    }

    //成功
    protected JsonResults BuildSuccessJson(String msg) {
        JsonResults bean = new JsonResults();
        bean.setMsg(msg);
        bean.setCode(ResultCodeEnum.SUCCESS.getCode());
        return bean;
    }

    //成功
    protected <T> JsonResults<T> BuildSuccessJson(T data, String msg) {
        JsonResults<T> bean = new JsonResults<T>();
        bean.setMsg(msg);
        bean.setData(data);
        bean.setCode(ResultCodeEnum.SUCCESS.getCode());
        return bean;
    }

    //成功
    protected <T> JsonResults<T> BuildSuccessJson(T data, Long total, String msg) {
        JsonResults<T> bean = new JsonResults<T>();
        bean.setMsg(msg);
        bean.setData(data);
        bean.setCode(ResultCodeEnum.SUCCESS.getCode());
        bean.setTotal(total);
        return bean;
    }

    protected boolean validMobile(String mobile) {
        if (mobile == null || mobile.isBlank()) {
            return false;
        }
        return mobile.length() == 11 && StringUtils.isNumeric(mobile);
    }


    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress="47.111.178.164";
        }
        // ipAddress = this.getRequest().getRemoteAddr();

        return ipAddress;
    }




    protected boolean invalidMobile(String mobile) {
        return !validMobile(mobile);
    }


}
