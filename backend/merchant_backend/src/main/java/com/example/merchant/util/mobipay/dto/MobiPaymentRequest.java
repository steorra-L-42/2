package com.example.merchant.util.mobipay.dto;

import com.example.merchant.domain.payment.dto.PaymentRequest;
import com.example.merchant.global.enums.MerchantType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MobiPaymentRequest {

    private MerchantType type;
    private Integer paymentBalance;
    private String carNumber;
    private String info;
    private Long merchantId;

    public static MobiPaymentRequest of(PaymentRequest request, Long merchantId) {
        return MobiPaymentRequest.builder()
                .type(request.getType())
                .paymentBalance(request.getPaymentBalance())
                .carNumber(request.getCarNumber())
                .info(request.getInfo())
                .merchantId(merchantId)
                .build();
    }

}
