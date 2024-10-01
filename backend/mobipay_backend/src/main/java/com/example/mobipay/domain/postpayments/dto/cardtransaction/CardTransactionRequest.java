package com.example.mobipay.domain.postpayments.dto.cardtransaction;

import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import com.example.mobipay.domain.postpayments.dto.ApprovalPaymentRequest;
import com.example.mobipay.global.dto.Header;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CardTransactionRequest {

    @JsonProperty("Header")
    private final Header header;

    private final String cardNo;
    private final String cvc;
    private final Long merchantId;
    private final Long paymentBalance;

    public CardTransactionRequest(String apiName, String apiKey, String userKey, OwnedCard ownedCard,
                                  ApprovalPaymentRequest request) {
        this.header = Header.of(apiName, apiKey, userKey);
        this.cardNo = ownedCard.getCardNo();
        this.cvc = ownedCard.getCvc();
        this.merchantId = request.getMerchantId();
        this.paymentBalance = request.getPaymentBalance();
    }
}
