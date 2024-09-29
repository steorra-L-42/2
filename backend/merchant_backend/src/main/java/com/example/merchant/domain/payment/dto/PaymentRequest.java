package com.example.merchant.domain.payment.dto;

import com.example.merchant.global.enums.MerchantType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class PaymentRequest {

    @NotNull(message = "Merchant type is empty")
    private MerchantType type;

    @NotNull(message = "Payment balance is empty")
    @Min(value = 1, message = "Payment balance must be greater than 0")
    private Integer paymentBalance;

    @NotBlank(message = "Car number is empty")
    @Size(min = 8, max = 8, message = "Car number must be 8 characters")
    private String carNumber;

    @NotNull(message = "Payment info is null")
    private String info;
}
