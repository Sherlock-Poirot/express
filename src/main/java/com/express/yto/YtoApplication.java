package com.express.yto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {
    SpringDataWebAutoConfiguration.class
})
@EnableAsync
@EnableScheduling
public class YtoApplication {

    public static void main(String[] args) {
        SpringApplication.run(YtoApplication.class, args);
    }

}
