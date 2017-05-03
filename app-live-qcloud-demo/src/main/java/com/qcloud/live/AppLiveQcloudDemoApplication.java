package com.qcloud.live;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppLiveQcloudDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(new String[] { "classpath*:config/spring-mvc.xml" }, args);

	}
}
