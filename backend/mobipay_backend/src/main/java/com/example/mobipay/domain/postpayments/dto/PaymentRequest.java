package com.example.mobipay.domain.postpayments.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class PaymentRequest {

    @NotNull(message = "empty type")
    private String type;

    @NotNull(message = "empty amount")
    private Long paymentBalance;

    @NotNull(message = "empty carNumber")
    private String carNumber;

    @NotNull(message = "empty info")
    private String info;

    @NotNull(message = "empty merchantId")
    private Long merchantId;
}
