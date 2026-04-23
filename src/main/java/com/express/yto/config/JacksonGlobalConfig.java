//package com.express.yto.config;
//
//import com.fasterxml.jackson.databind.MapperFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
//
///**
// * @author Detective
// * @date Created in 2026/4/23
// */
//@Configuration
//public class JacksonGlobalConfig {
//
//    @Bean
//    public ObjectMapper objectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        // 开启标准命名规则，彻底解决 kName 变成 kname 的问题
//        objectMapper.enable(MapperFeature.USE_STD_BEAN_NAMING);
//
//        return objectMapper;
//    }
//}
