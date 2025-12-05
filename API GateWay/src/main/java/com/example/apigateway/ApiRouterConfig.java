package com.example.apigateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiRouterConfig {

    private AdminCheckFilter adminCheckFilter;

    ApiRouterConfig(AdminCheckFilter adminCheckFilter) {
        this.adminCheckFilter = adminCheckFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, JwtCookieFilter jwtFilter) {
        return builder.routes()
//                -------------------------------------------------
                .route("song-player",r-> r
                        .path("/play/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://PLAYER-SERVICE"))
//                ---------------------------------------------------
                .route("search",r -> r
                        .path("/search/**")
                        .filters( f -> f.stripPrefix(1).filter(jwtFilter))
                        .uri("lb://SEARCHENGINE-MICROSERVICE"))
//                ---------------------------------------------------
                .route("ai-service-route", r -> r
                        .path("/yt-ai/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter))
                        .uri("lb://YT-AI"))
//                ---------------------------------------------------
                .route("security",r -> r
                        .path("/auth/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://SECURITY-MICROSERVICE"))
                //                --------------------------------------------------
                .route("admin-upload", r -> r
                        .path("/s3/upload/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(adminCheckFilter))
                        .uri("lb://YT-S3-MICROSERVICE"))
//                ---------------------------------------------------
                .route("admin-delete", r -> r
                        .path("s3/delete/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .filter(adminCheckFilter))
                        .uri("lb://SEARCHENGINE-MICROSERVICE"))
                .build();
    }
}
