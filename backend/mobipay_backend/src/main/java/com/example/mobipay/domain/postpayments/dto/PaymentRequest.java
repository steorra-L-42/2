package com.example.mobipay.domain.postpayments.dto;

import jakarta.validation.constraints.Min;
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

    @NotNull(message = "empty type")
    private String type;

    @NotNull(message = "empty amount")
    @Min(value = 1, message = "Payment balance must be greater than 0")
    private Long paymentBalance;

    @NotNull(message = "empty carNumber")
    @Size(min = 7, max = 8, message = "Car number must be 7~8 characters")
    private String carNumber;

    @NotNull(message = "empty info")
    private String info;

    @NotNull(message = "empty merchantId")
    private Long merchantId;
}
