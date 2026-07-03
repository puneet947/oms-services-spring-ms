package com.oms.order.dto;

import java.math.BigDecimal;

public record CreateOrderRequest(String productCode, int quantity, BigDecimal amount) {
}
