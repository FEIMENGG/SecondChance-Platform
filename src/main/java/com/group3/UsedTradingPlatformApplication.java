package com.group3;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author group3
 * @date 2024/04
 */

@SpringBootApplication
@EntityScan(basePackages = "com.group3.*")
@ComponentScan({"com.group3.*"})
@MapperScan({"com.group3.dao"})
@EnableAsync
public class UsedTradingPlatformApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(UsedTradingPlatformApplication.class).run(args);
    }

}
