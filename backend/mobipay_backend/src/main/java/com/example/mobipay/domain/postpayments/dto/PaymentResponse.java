package com.example.mobipay.domain.postpayments.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {

    private Long approvalWaitingId;
    private Long carId;
    private Long merchantId;
    private Long paymentBalance;
}
