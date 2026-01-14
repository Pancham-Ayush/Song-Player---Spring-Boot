package com.example.MIcroService_Player;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MicroServicePlayerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroServicePlayerApplication.class, args);
	}

}
