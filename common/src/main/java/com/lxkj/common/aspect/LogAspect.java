package com.lxkj.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class LogAspect {
    @Pointcut("within(com.lxkj.controller..*)")
    public void printLog() {}

    @Before("printLog()")
    public void reqMessage(JoinPoint joinPoint) {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        StringBuffer buff=new StringBuffer();
        buff.append("{");
        Map properties = request.getParameterMap();
        Iterator entries = properties.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            String name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            String value = "";
            if(null == valueObj){
                value = "";
            }else if(valueObj instanceof String[]){
                String[] values = (String[])valueObj;
                for(int i=0;i<values.length;i++){
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length()-1);
            }else{
                value = valueObj.toString();
            }
            buff.append(name+":"+value+",");
        }
        buff.append("}");
        log.info("********************接受请求********************");
        log.info("Url : " + request.getRequestURL().toString());
        log.info("Controller : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        log.info("Method : " + request.getMethod());
        log.info("Params : " +buff.toString());
        log.info("Entity : " + Arrays.toString(joinPoint.getArgs()));
        log.info("Header : " +  "token="+request.getHeader("token"));
        log.info("IP : " + request.getRemoteAddr());
        log.info("*************************************************");
    }


}
