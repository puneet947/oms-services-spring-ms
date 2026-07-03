package com.oms.payment.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Payment {
    private String id;
    private String orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public Payment() {
    }

    public Payment(String id, String orderId, BigDecimal amount, PaymentStatus status) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
