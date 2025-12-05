package com.example.ai;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableFeignClients
@SpringBootApplication
public class YT_AI_Application {

	public static void main(String[] args) {
		SpringApplication.run(YT_AI_Application.class, args);
	}

}
