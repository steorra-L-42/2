package com.example.mobipay.domain.postpayments.dto;

import com.example.mobipay.domain.approvalwaiting.entity.ApprovalWaiting;
import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.merchant.entity.Merchant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {

    private Long approvalWaitingId;
    private Long carId;
    private Long merchantId;
    private Long paymentBalance;

    public static PaymentResponse of(ApprovalWaiting approvalWaiting, Car car, Merchant merchant,
                                     PaymentRequest request) {
        return PaymentResponse.builder()
                .approvalWaitingId(approvalWaiting.getId())
                .carId(car.getId())
                .merchantId(merchant.getId())
                .paymentBalance(request.getPaymentBalance())
                .build();
    }
}
