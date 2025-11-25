package com.example.Music_Player.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class VirtualThreadConfig {
    @Bean
    public Executor VirtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
