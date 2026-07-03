package com.oms.payment.controller;

import java.util.Collection;

import com.oms.payment.dto.PaymentRequest;
import com.oms.payment.dto.PaymentResponse;
import com.oms.payment.model.Payment;
import com.oms.payment.service.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/charge")
    PaymentResponse charge(@RequestBody PaymentRequest request) {
        return paymentService.charge(request);
    }

    @PostMapping("/refund")
    PaymentResponse refund(@RequestBody PaymentRequest request) {
        return paymentService.refund(request);
    }

    @GetMapping
    Collection<Payment> list() {
        return paymentService.findAll();
    }
}
