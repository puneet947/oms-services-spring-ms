package com.oms.payment.dto;

public record PaymentResponse(String paymentId, String orderId, String status, String message) {
}
