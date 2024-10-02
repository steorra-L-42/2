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
    private final String cardName;
    private final Long merchantId;
    private final Double lat;
    private final Double lng;

    public static ReceiptResponse from(MerchantTransaction merchantTransaction) {
        var merchant = merchantTransaction.getMerchant();
        var registeredCard = merchantTransaction.getRegisteredCard();
        var ownedCard = registeredCard.getOwnedCard();
        var cardProduct = ownedCard.getCardProduct();

        return ReceiptResponse.builder()
                .transactionUniqueNo(merchantTransaction.getTransactionUniqueNo())
                .transactionDate(merchantTransaction.getTransactionDate())
                .transactionTime(merchantTransaction.getTransactionTime())
                .paymentBalance(merchantTransaction.getPaymentBalance())
                .info(merchantTransaction.getInfo())
                .merchantName(merchant.getMerchantName())
                .cardNo(ownedCard.getCardNo())
                .cardName(cardProduct.getCardName())
                .merchantId(merchant.getId())
                .lat(merchant.getLat())
                .lng(merchant.getLng())
                .build();
    }
}
