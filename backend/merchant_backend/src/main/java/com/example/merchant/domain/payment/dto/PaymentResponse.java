package com.example.merchant.domain.payment.dto;

import com.example.merchant.util.mobipay.dto.MobiPaymentResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {

    private Long approvalWaitingId;
    private Long carId;
    private Long merchantId;
    private Integer paymentBalance;

    public static PaymentResponse from(MobiPaymentResponse mobiPaymentResponse) {
        if(mobiPaymentResponse == null) { // 4XX error 일때 null이 들어올 수 있음. 그대로 전달.
            return null;
        }
        return PaymentResponse.builder()
            .approvalWaitingId(mobiPaymentResponse.getApprovalWaitingId())
            .carId(mobiPaymentResponse.getCarId())
            .merchantId(mobiPaymentResponse.getMerchantId())
            .paymentBalance(mobiPaymentResponse.getPaymentBalance())
            .build();
    }

}
