package com.example.merchant.domain.cancel.dto;

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
public class MerchantTransaction {
    @NotNull
    private Long transactionUniqueNo;
    @NotNull
    @Size(min = 8, max = 8)
    private String transactionDate;
    @NotNull
    @Size(min = 8, max = 8)
    private String transactionTime;
    @NotNull
    @Min(value = 1, message = "paymentBalance must be greater than 0")
    private Long paymentBalance;
    @NotNull
    private String info;
    @NotNull
    private boolean cancelled;
    @NotNull
    private Long merchantId;
    @NotNull
    private Long mobiUserId;
    @NotNull
    private Long ownedCardId;
}
