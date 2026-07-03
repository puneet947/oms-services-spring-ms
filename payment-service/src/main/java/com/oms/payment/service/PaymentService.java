package com.oms.payment.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.oms.payment.dto.PaymentRequest;
import com.oms.payment.dto.PaymentResponse;
import com.oms.payment.model.Payment;
import com.oms.payment.model.PaymentStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PaymentService {

    private static final BigDecimal DEMO_PAYMENT_LIMIT = new BigDecimal("5000.00");

    private final Map<String, Payment> paymentsByOrderId = new ConcurrentHashMap<>();

    public PaymentResponse charge(PaymentRequest request) {
        validate(request);
        if (request.amount().compareTo(DEMO_PAYMENT_LIMIT) > 0) {
            Payment failed = new Payment(UUID.randomUUID().toString(), request.orderId(), request.amount(), PaymentStatus.FAILED);
            paymentsByOrderId.put(request.orderId(), failed);
            return new PaymentResponse(failed.getId(), request.orderId(), "FAILED", "Amount exceeds demo payment limit");
        }

        Payment payment = new Payment(UUID.randomUUID().toString(), request.orderId(), request.amount(), PaymentStatus.PAID);
        paymentsByOrderId.put(request.orderId(), payment);
        return new PaymentResponse(payment.getId(), request.orderId(), "PAID", "Payment captured");
    }

    public PaymentResponse refund(PaymentRequest request) {
        Payment payment = paymentsByOrderId.get(request.orderId());
        if (payment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found for order");
        }
        if (payment.getStatus() != PaymentStatus.PAID) {
            return new PaymentResponse(payment.getId(), request.orderId(), payment.getStatus().name(), "Payment is not refundable");
        }
        payment.setStatus(PaymentStatus.REFUNDED);
        return new PaymentResponse(payment.getId(), request.orderId(), "REFUNDED", "Payment refunded");
    }

    public Collection<Payment> findAll() {
        return paymentsByOrderId.values();
    }

    private void validate(PaymentRequest request) {
        if (request.orderId() == null || request.orderId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "orderId is required");
        }
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount must be positive");
        }
    }
}
