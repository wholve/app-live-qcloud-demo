package com.qcloud.live;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class AppLiveQcloudDemoApplication extends SpringBootServletInitializer{

	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(new String[]{
                "classpath*:config/spring-mvc.xml"
        });
    }
    public static void main(String[] args) {
        SpringApplication.run(new String[]{
                "classpath*:config/spring-mvc.xml"
        }, args);

    }
}
