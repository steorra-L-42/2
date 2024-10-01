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
 * MER -> POS
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class PaymentResult {

    @NotNull(message = "Success is empty")
    private Boolean success;

    @NotNull(message = "Type is empty")
    private MerchantType type;

    @NotNull(message = "Payment balance is empty")
    @Min(value = 1, message = "Payment balance must be greater than 0")
    private Integer paymentBalance;

    @NotNull(message = "Payment info is null")
    private String info;

    public static PaymentResult of(PaymentResultRequest result, MerchantType type) {
        return new PaymentResult(result.getSuccess(), type, result.getPaymentBalance(), result.getInfo());
    }
}
