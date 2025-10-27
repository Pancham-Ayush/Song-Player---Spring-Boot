package com.example.Music_Player;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableFeignClients
public class MusicPlayerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MusicPlayerApplication.class, args);
	}

}
