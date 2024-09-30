package com.example.merchant.util.mobipay.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class MobiPaymentResponse {

    @NotNull(message = "Approval waiting id is null")
    private Long approvalWaitingId;

    @NotNull(message = "Car id is null")
    private Long carId;

    @NotNull(message = "Merchant id is null")
    private Long merchantId;

    @NotNull(message = "Payment balance is empty")
    @Min(value = 1, message = "Payment balance must be greater than 0")
    private Integer paymentBalance;

}
