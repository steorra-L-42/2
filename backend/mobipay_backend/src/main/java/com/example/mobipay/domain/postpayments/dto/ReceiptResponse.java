package com.example.mobipay.domain.postpayments.dto;

import com.example.mobipay.domain.merchanttransaction.entity.MerchantTransaction;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReceiptResponse {

    private final Long transactionUniqueNo;
    private final String transactionDate;
    private final String transactionTime;
    private final Long paymentBalance;
    private final String info;
    private final String merchantName;
    private final String cardNo;


    public static ReceiptResponse from(MerchantTransaction merchantTransaction) {
        return ReceiptResponse.builder()
                .transactionUniqueNo(merchantTransaction.getTransactionUniqueNo())
                .transactionDate(merchantTransaction.getTransactionDate())
                .transactionTime(merchantTransaction.getTransactionTime())
                .paymentBalance(merchantTransaction.getPaymentBalance())
                .info(merchantTransaction.getInfo())
                .merchantName(merchantTransaction.getMerchant().getMerchantName())
                .cardNo(merchantTransaction.getRegisteredCard().getOwnedCard().getCardNo())
                .build();
    }
}
