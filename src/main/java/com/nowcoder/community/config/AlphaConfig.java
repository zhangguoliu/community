package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.config
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-13  18:15
 * @Description: TODO
 * @Version: 1.0
 */
@Configuration  // 表示此类是一个配置类，它的方法可供 Spring 容器获取外部 jar 包中的 Bean
// @Configuration 底层是基于 @Component 的
// @SpringBootApplication 也可以表示一个配置类，但主要用于注解应用程序入口类
public class AlphaConfig {
    @Bean   // @Bean 可以装配任何 Bean，除了装配第三方 Bean，也可以装配自定义的 Bean
    public SimpleDateFormat simpleDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
