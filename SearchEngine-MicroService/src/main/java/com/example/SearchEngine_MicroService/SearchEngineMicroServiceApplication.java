package com.example.SearchEngine_MicroService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class SearchEngineMicroServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchEngineMicroServiceApplication.class, args);
	}

}
