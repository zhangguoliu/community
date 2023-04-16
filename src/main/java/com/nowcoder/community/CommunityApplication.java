package com.nowcoder.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication 下包含
// @SpringBootConfiguration 此注解说明 CommunityApplication 是一个配置类
// @EnableAutoConfiguration
// @ComponentScan 等注解
@SpringBootApplication
public class CommunityApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
