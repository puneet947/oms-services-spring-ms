package com.oms.order.dto;

import java.math.BigDecimal;

public record PaymentRequest(String orderId, BigDecimal amount) {
}
