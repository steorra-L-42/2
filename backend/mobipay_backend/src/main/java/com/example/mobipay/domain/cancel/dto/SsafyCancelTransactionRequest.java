package com.example.mobipay.domain.cancel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.mobipay.global.dto.Header;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SsafyCancelTransactionRequest {

    @JsonProperty("Header")
    private final Header header;

    private final String cardNo;
    private final String cvc;
    private final Long transactionUniqueNo;

    @Builder
    public SsafyCancelTransactionRequest(String apiName, String apiKey, String userKey, String cardNo, String cvc, Long transactionUniqueNo) {
        this.header = Header.of(apiName, apiKey, userKey);
        this.cardNo = cardNo;
        this.cvc = cvc;
        this.transactionUniqueNo = transactionUniqueNo;
    }
}