package com.example.mobipay.domain.postpayments.controller;

import com.example.mobipay.domain.postpayments.dto.ApprovalPaymentRequest;
import com.example.mobipay.domain.postpayments.dto.ApprovalPaymentResponse;
import com.example.mobipay.domain.postpayments.dto.PaymentRequest;
import com.example.mobipay.domain.postpayments.dto.PaymentResponse;
import com.example.mobipay.domain.postpayments.dto.ReceiptResponse;
import com.example.mobipay.domain.postpayments.dto.historyresponse.HistoryListResponse;
import com.example.mobipay.domain.postpayments.service.PostPaymentsApprovalService;
import com.example.mobipay.domain.postpayments.service.PostPaymentsRequestService;
import com.example.mobipay.domain.postpayments.service.PostPaymentsUtilService;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final PostPaymentsApprovalService postPaymentsApprovalService;
    private final PostPaymentsUtilService postPaymentsUtilService;

    @PostMapping("/request")
    public ResponseEntity<PaymentResponse> requestPayment(@RequestHeader("mobiApiKey") @NotNull String mobiApiKey,
                                                          @RequestBody @Valid PaymentRequest request) {

        PaymentResponse paymentResponse = postPaymentsRequestService.sendRequestToCarGroup(request, mobiApiKey);

        return ResponseEntity.ok(paymentResponse);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/approval")
    public ResponseEntity<ApprovalPaymentResponse> startPayment(@RequestBody @Valid ApprovalPaymentRequest request,
                                                                @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        ApprovalPaymentResponse approvalPaymentResponse = postPaymentsApprovalService.processPaymentApproval(request,
                oAuth2User);

        return ResponseEntity.ok(approvalPaymentResponse);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/receipt/{transaction_unique_no}")
    public ResponseEntity<ReceiptResponse> getReceipt(
            @PathVariable("transaction_unique_no") @Positive Long transactionUniqueNo,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        ReceiptResponse response = postPaymentsUtilService.getReceipt(transactionUniqueNo, oAuth2User);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/history")
    public ResponseEntity<HistoryListResponse> getHistories(@AuthenticationPrincipal CustomOAuth2User oAuth2User) {

        HistoryListResponse response = postPaymentsUtilService.getHistories(oAuth2User);

        if (response.getItems().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }
}
