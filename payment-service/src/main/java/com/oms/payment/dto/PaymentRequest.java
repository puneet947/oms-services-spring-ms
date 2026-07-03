package com.oms.payment.dto;

import java.math.BigDecimal;

public record PaymentRequest(String orderId, BigDecimal amount) {
}
