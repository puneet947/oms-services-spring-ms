package com.oms.order.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.oms.order.client.PaymentClient;
import com.oms.order.dto.CreateOrderRequest;
import com.oms.order.dto.PaymentRequest;
import com.oms.order.model.Order;
import com.oms.order.model.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderSagaService {

    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    private final PaymentClient paymentClient;

    public OrderSagaService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public Order createOrder(CreateOrderRequest request, String username) {
        validate(request);
        Order order = new Order(UUID.randomUUID().toString(), username, request.productCode(), request.quantity(), request.amount());
        orders.put(order.getId(), order);

        var payment = paymentClient.charge(new PaymentRequest(order.getId(), order.getAmount()));
        if ("PAID".equals(payment.status())) {
            order.setPaymentId(payment.paymentId());
            order.setStatus(OrderStatus.CONFIRMED);
            order.setMessage("Order confirmed after successful payment");
            return order;
        }

        order.setStatus(OrderStatus.PAYMENT_FAILED);
        order.setMessage(payment.message());
        return order;
    }

    public Collection<Order> findAll() {
        return orders.values();
    }

    public Order findById(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        return order;
    }

    public Order cancelOrder(String orderId) {
        Order order = findById(orderId);
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            order.setStatus(OrderStatus.CANCELLED);
            order.setMessage("Order cancelled without refund because it was not confirmed");
            return order;
        }

        order.setStatus(OrderStatus.REFUND_PENDING);
        var refund = paymentClient.refund(new PaymentRequest(order.getId(), order.getAmount()));
        if ("REFUNDED".equals(refund.status())) {
            order.setStatus(OrderStatus.REFUNDED);
            order.setMessage("Order cancelled and payment refunded");
        } else {
            order.setStatus(OrderStatus.REFUND_PENDING);
            order.setMessage(refund.message());
        }
        return order;
    }

    private void validate(CreateOrderRequest request) {
        if (request.productCode() == null || request.productCode().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productCode is required");
        }
        if (request.quantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "quantity must be positive");
        }
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount must be positive");
        }
    }
}
