package com.example.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication

public class ApiGateWayApplication {

    private static final Logger log = LoggerFactory.getLogger(ApiGateWayApplication.class);

    public static void main(String[] args) {
		SpringApplication.run(ApiGateWayApplication.class, args);
	}

    @Autowired
    protected AdminCheckFilter adminCheckFilter;
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, JwtCookieFilter jwtFilter) {
        log.info("Custom Route Locator"+ builder.routes());
        return builder.routes()
                .route("core-service-admin-route", r -> r
                        .path("/core/upload/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(adminCheckFilter)
                        )
                        .uri("lb://CORE-SERVICE"))
                .route("core-service-route", r -> r
                        .path("/core/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter))
                        .uri("lb://CORE-SERVICE"))
                .route("song-player",r-> r
                        .path("/play/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://PLAYER-SERVICE")
                )
                .route("security",r -> r
                        .path("/auth/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://SECURITY-MICROSERVICE"))
                .build();
    }

}
