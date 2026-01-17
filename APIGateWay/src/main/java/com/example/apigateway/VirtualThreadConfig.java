package com.example.apigateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class VirtualThreadConfig {
    @Bean("Virtual")
    public Executor getExecutorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
