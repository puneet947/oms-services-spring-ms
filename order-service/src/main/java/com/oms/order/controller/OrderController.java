package com.oms.order.controller;

import java.util.Collection;

import com.oms.order.dto.CreateOrderRequest;
import com.oms.order.model.Order;
import com.oms.order.service.OrderSagaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderSagaService orderSagaService;

    public OrderController(OrderSagaService orderSagaService) {
        this.orderSagaService = orderSagaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Order create(@RequestBody CreateOrderRequest request, @RequestHeader("X-User") String username) {
        return orderSagaService.createOrder(request, username);
    }

    @GetMapping
    Collection<Order> list() {
        return orderSagaService.findAll();
    }

    @GetMapping("/{orderId}")
    Order get(@PathVariable String orderId) {
        return orderSagaService.findById(orderId);
    }

    @PostMapping("/{orderId}/cancel")
    Order cancel(@PathVariable String orderId) {
        return orderSagaService.cancelOrder(orderId);
    }
}
