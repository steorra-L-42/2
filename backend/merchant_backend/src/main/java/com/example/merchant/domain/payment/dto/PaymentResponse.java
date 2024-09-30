package com.example.merchant.domain.payment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {

    private Long approvalWaitingId;
    private Long carId;
    private Long merchantId;
    private Integer paymentBalance;

    public static PaymentResponse of(Long approvalWaitingId, Long carId, Long merchantId, Integer paymentBalance) {
        return PaymentResponse.builder()
            .approvalWaitingId(approvalWaitingId)
            .carId(carId)
            .merchantId(merchantId)
            .paymentBalance(paymentBalance)
            .build();
    }

}
