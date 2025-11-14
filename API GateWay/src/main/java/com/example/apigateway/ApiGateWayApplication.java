package com.example.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication

public class ApiGateWayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGateWayApplication.class, args);
	}

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        return builder.routes()
                .route("core-service-route", r -> r
                        .path("/core/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://CORE-SERVICE"))
                .route("song-player",x-> x
                        .path("/play/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://PLAYER-SERVICE")
                )
                .build();
    }

}
