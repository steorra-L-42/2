package com.example.merchant.domain.payment.dto;

import com.example.merchant.global.enums.MerchantType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Payment result
 * MOBI -> MER
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class PaymentResultRequest {

    @NotNull(message = "Success is empty")
    private Boolean success;

    @NotNull(message = "Merchant Id is empty")
    private Long merchantId;

    @NotNull(message = "Payment balance is empty")
    @Min(value = 1, message = "Payment balance must be greater than 0")
    private Integer paymentBalance;

    @NotNull(message = "Payment info is null")
    private String info;
}
