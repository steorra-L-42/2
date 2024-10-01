package com.example.mobipay.domain.postpayments.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApprovalPaymentResponse {

    private Long approvalWaitingId;
    private Long merchantId;
    private Long paymentBalance;
    private String cardNo;
    private Boolean approved;

    public static ApprovalPaymentResponse from(ApprovalPaymentRequest request) {

        return ApprovalPaymentResponse.builder()
                .approvalWaitingId(request.getApprovalWaitingId())
                .merchantId(request.getMerchantId())
                .paymentBalance(request.getPaymentBalance())
                .cardNo(request.getCardNo())
                .approved(request.getApproved())
                .build();
    }
}
