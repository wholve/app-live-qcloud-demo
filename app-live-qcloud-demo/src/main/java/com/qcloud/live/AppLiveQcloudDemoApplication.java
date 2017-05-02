package com.qcloud.live;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration
public class AppLiveQcloudDemoApplication extends SpringBootServletInitializer{

	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(new String[]{
                "classpath*:config/spring-mvc.xml",
        });
    }
    public static void main(String[] args) {
        SpringApplication.run(new String[]{
                "classpath*:config/spring-mvc.xml",
        }, args);

    }
}
