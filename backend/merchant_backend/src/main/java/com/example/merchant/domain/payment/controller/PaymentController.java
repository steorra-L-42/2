package com.example.merchant.domain.payment.controller;

import com.example.merchant.domain.payment.dto.PaymentResponse;
import com.example.merchant.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/merchants/payments")
@RestController
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/request")
    public ResponseEntity<PaymentResponse> request() {

       PaymentResponse response = paymentService.request();

        return ResponseEntity.ok(response);
    }

   @PostMapping("/result")
    public ResponseEntity<PaymentResponse> result() {

        paymentService.result();

        return ResponseEntity.ok().build();
    }
}
