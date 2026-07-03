package com.oms.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutes {

    @Bean
    RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", route -> route.path("/auth/**")
                        .uri("lb://AUTH-SERVICE"))
                .route("order-service", route -> route.path("/orders/**")
                        .filters(filters -> filters.circuitBreaker(config -> config
                                .setName("orderCircuitBreaker")
                                .setFallbackUri("forward:/fallback/orders")))
                        .uri("lb://ORDER-SERVICE"))
                .route("payment-service", route -> route.path("/payments/**")
                        .filters(filters -> filters.circuitBreaker(config -> config
                                .setName("paymentCircuitBreaker")
                                .setFallbackUri("forward:/fallback/payments")))
                        .uri("lb://PAYMENT-SERVICE"))
                .build();
    }
}
