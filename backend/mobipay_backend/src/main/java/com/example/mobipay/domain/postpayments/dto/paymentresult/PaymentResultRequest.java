package com.example.mobipay.domain.postpayments.dto.paymentresult;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentResultRequest {

    private final Boolean success;
    private final Long merchantId;
    private final Long paymentBalance;
    private final String info;
}
