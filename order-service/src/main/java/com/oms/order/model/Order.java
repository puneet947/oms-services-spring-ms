package com.oms.order.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Order {
    private String id;
    private String username;
    private String productCode;
    private int quantity;
    private BigDecimal amount;
    private OrderStatus status;
    private String paymentId;
    private String message;
    private Instant createdAt;
    private Instant updatedAt;

    public Order() {
    }

    public Order(String id, String username, String productCode, int quantity, BigDecimal amount) {
        this.id = id;
        this.username = username;
        this.productCode = productCode;
        this.quantity = quantity;
        this.amount = amount;
        this.status = OrderStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getProductCode() {
        return productCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
        this.updatedAt = Instant.now();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        this.updatedAt = Instant.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
