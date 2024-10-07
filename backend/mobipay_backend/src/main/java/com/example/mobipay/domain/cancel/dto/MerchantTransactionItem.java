package com.example.mobipay.domain.cancel.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.example.mobipay.domain.merchanttransaction.entity.MerchantTransaction;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Builder
public class MerchantTransactionItem {
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

    public static MerchantTransactionItem from(MerchantTransaction merchantTransaction) {
        return MerchantTransactionItem.builder()
                .transactionUniqueNo(merchantTransaction.getTransactionUniqueNo())
                .transactionDate(merchantTransaction.getTransactionDate())
                .transactionTime(merchantTransaction.getTransactionTime())
                .paymentBalance(merchantTransaction.getPaymentBalance())
                .info(merchantTransaction.getInfo())
                .cancelled(merchantTransaction.getCancelled())
                .merchantId(merchantTransaction.getMerchant().getId())
                .mobiUserId(merchantTransaction.getRegisteredCard().getMobiUserId())
                .ownedCardId(merchantTransaction.getRegisteredCard().getOwnedCardId())
                .build();
    }
}
