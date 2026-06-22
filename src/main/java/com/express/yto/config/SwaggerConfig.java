package com.express.yto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author Detective
 * @date Created in 2024/6/22
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30) // 3.0 版本使用 OAS_30
                .apiInfo(apiInfo())
                .select()
                // 此处自行修改为自己的 Controller 包路径
                .apis(RequestHandlerSelectors.basePackage("com.express.yto.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("圆通快递对账 项目接口文挡")
                .description("本文档描述 圆通快递对账平项目接口定义")
                .version("1.0")
                .contact(new Contact("Detective Stone", null, "13750846673@163.com"))
                .build();
    }
}
