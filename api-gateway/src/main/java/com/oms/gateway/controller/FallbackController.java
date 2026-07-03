package com.oms.gateway.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/orders")
    Mono<ResponseEntity<Map<String, Object>>> ordersFallback() {
        return Mono.just(unavailable("order-service"));
    }

    @RequestMapping("/payments")
    Mono<ResponseEntity<Map<String, Object>>> paymentsFallback() {
        return Mono.just(unavailable("payment-service"));
    }

    private ResponseEntity<Map<String, Object>> unavailable(String serviceName) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "timestamp", Instant.now().toString(),
                        "service", serviceName,
                        "message", "Service is temporarily unavailable"));
    }
}
