package com.example.apigateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class ApiGatewayCorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {

        CorsConfiguration cors = new CorsConfiguration();

        // Allowed frontend origins
        cors.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173"
        ));

        cors.setAllowCredentials(true);
        cors.addAllowedHeader("*");
        cors.addAllowedMethod("*"); // GET, POST, PUT, DELETE, OPTIONS

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);

        return new CorsWebFilter(source);
    }
}
