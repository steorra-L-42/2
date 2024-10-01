package com.example.merchant.domain.payment.controller;

import com.example.merchant.domain.payment.dto.PaymentRequest;
import com.example.merchant.domain.payment.dto.PaymentResponse;
import com.example.merchant.domain.payment.dto.PaymentResultRequest;
import com.example.merchant.domain.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/merchants/payments")
@RestController
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/request")
    public ResponseEntity<PaymentResponse> request(@RequestHeader("merApiKey") String merApiKey,
                                                   @RequestBody @Valid PaymentRequest request) {

        return paymentService.request(merApiKey, request);
    }

   @PostMapping("/result")
    public ResponseEntity<PaymentResponse> result(@RequestHeader("merApiKey") String merApiKey,
                                                  @RequestBody @Valid PaymentResultRequest request) {

        paymentService.result(merApiKey, request);

        return ResponseEntity.ok().build();
    }
}
