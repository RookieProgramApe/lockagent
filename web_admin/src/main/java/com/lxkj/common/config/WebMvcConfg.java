package com.lxkj.common.config;

import com.lxkj.annotation.LoginRequired;
import com.lxkj.common.exception.TokenException;
import com.lxkj.entity.Member;
import com.lxkj.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Configuration
public class WebMvcConfg implements WebMvcConfigurer {

    @Autowired
    private MemberService memberService;
    @Value("${devmode}")
    private Boolean devmode;
    /**
     * 添加静态资源文件，外部可以直接访问地址
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //需要告知系统，这是要被当成静态文件的！
        //第一个方法设置访问路径前缀，第二个方法设置资源路径
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        if (devmode) {
            registry.addResourceHandler("/static/downFile/**").addResourceLocations("file:D:\\projects\\");
        }
    }

    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //API接口Token拦截器
        HandlerInterceptor TokenInterceptor = new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                // 如果不是映射到方法直接通过
                if (!(handler instanceof HandlerMethod)) {
                    return true;
                }
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                Method method = handlerMethod.getMethod();
                // 有 @LoginRequired 注解，需要登录认证
                LoginRequired methodAnnotation = method.getAnnotation(LoginRequired.class);
                if (methodAnnotation != null) {
                    String token = request.getHeader("token");  // 从 http 请求头中取出 token
                    if (token != null && !token.isBlank()) {
                        Member member = memberService.getById(token);
                        if (member != null) {
                            request.setAttribute("Member", member);
                            return true;
                        }
                    }
                    throw new TokenException("未登录");
                }
                return true;
            }
        };
        registry.addInterceptor(TokenInterceptor).addPathPatterns("/api/**");
    }

}
