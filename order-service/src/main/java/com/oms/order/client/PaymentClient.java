package com.oms.order.client;

import com.oms.order.dto.PaymentRequest;
import com.oms.order.dto.PaymentResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PaymentClient {

    private final WebClient.Builder webClientBuilder;

    public PaymentClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Retry(name = "paymentRetry")
    @CircuitBreaker(name = "paymentCircuitBreaker", fallbackMethod = "paymentFallback")
    public PaymentResponse charge(PaymentRequest request) {
        return webClientBuilder.build()
                .post()
                .uri("http://payment-service/payments/charge")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .block();
    }

    @Retry(name = "paymentRetry")
    @CircuitBreaker(name = "paymentCircuitBreaker", fallbackMethod = "refundFallback")
    public PaymentResponse refund(PaymentRequest request) {
        return webClientBuilder.build()
                .post()
                .uri("http://payment-service/payments/refund")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .block();
    }

    PaymentResponse paymentFallback(PaymentRequest request, Throwable throwable) {
        return new PaymentResponse(null, request.orderId(), "FAILED", "Payment service unavailable: " + throwable.getMessage());
    }

    PaymentResponse refundFallback(PaymentRequest request, Throwable throwable) {
        return new PaymentResponse(null, request.orderId(), "REFUND_FAILED", "Refund unavailable: " + throwable.getMessage());
    }
}
