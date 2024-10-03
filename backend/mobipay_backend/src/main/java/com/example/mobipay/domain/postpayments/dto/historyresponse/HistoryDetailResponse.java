package com.example.mobipay.domain.postpayments.dto.historyresponse;

import com.example.mobipay.domain.merchanttransaction.entity.MerchantTransaction;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HistoryDetailResponse {
    private Long transactionUniqueNo;
    private String merchantName;
    private String transactionDate;
    private String transactionTime;
    private Long paymentBalance;

    public static HistoryDetailResponse from(MerchantTransaction merchantTransaction) {
        return HistoryDetailResponse.builder()
                .transactionUniqueNo(merchantTransaction.getTransactionUniqueNo())
                .merchantName(merchantTransaction.getMerchant().getMerchantName())
                .transactionDate(merchantTransaction.getTransactionDate())
                .transactionTime(merchantTransaction.getTransactionTime())
                .paymentBalance(merchantTransaction.getPaymentBalance())
                .build();
    }
}
