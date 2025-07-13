package com.yusssss.sso.gatewayservice.core.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {


    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("user-service", r -> r.path("/api/users/**")
                        .filters(f -> f.rewritePath("/api/users/(?<segment>.*)", "/users/${segment}"))
                        .uri("lb://user-service"))

                .route("event-service", r -> r.path("/api/events/**")
                        .filters(f -> f.rewritePath("/api/events/(?<segment>.*)", "/events/${segment}"))
                        .uri("lb://event-service"))

                .route("ticket-service", r -> r.path("/api/tickets/**")
                        .filters(f -> f.rewritePath("/api/tickets/(?<segment>.*)", "/tickets/${segment}"))
                        .uri("lb://ticket-service"))

                .route("notification-service", r -> r.path("/api/notifications/**")
                        .filters(f -> f.rewritePath("/api/notifications/(?<segment>.*)", "/notifications/${segment}"))
                        .uri("lb://notification-service"))

                .build();
    }
}
