package com.express.yto;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class YtoApplication {

    public static void main(String[] args) {
        SpringApplication.run(YtoApplication.class, args);
    }

}
