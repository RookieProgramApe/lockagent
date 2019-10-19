package com.lxkj.common.config;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.http.MediaType.ALL_VALUE;

/**
 * swagger2相关配置
 * UI访问地址为：http://localhost:8088/lockagent/swagger-ui.html#
 */
@Configuration
@EnableSwagger2
public class Swagger2 {
    @Bean
    public Docket createRestApi() {
        //设置swagger处理的请求协议（默认配置form和json两种配置）
        Set<String> produces = new HashSet<String>();
        produces.add(ALL_VALUE);
        // produces.add(APPLICATION_JSON_VALUE);
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("合肥龙虓科技 RESTful APIs")
                        .license("域名统一请求地址：https://aw.wisehuitong.com/lockagent")
                        .description("商城公众号接口文档")
                        .version("1.0.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build().produces(produces);
    }
}
