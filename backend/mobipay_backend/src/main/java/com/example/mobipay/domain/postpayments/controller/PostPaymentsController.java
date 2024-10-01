package com.example.mobipay.domain.postpayments.controller;

import com.example.mobipay.domain.postpayments.dto.PaymentRequest;
import com.example.mobipay.domain.postpayments.dto.PaymentResponse;
import com.example.mobipay.domain.postpayments.service.PostPaymentsRequestService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/postpayments")
public class PostPaymentsController {

    private final PostPaymentsRequestService postPaymentsRequestService;

    @PostMapping("/request")
    public ResponseEntity<PaymentResponse> requestPayment(@RequestHeader("mobiApiKey") @NotNull String mobiApiKey,
                                                          @RequestBody @Valid PaymentRequest request) {

        postPaymentsRequestService.sendRequestToCarGroup(request, mobiApiKey);

        return ResponseEntity.ok(PaymentResponse.builder().build());
    }
}
