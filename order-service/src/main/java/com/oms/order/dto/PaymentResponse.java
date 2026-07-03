package com.oms.order.dto;

public record PaymentResponse(String paymentId, String orderId, String status, String message) {
}
