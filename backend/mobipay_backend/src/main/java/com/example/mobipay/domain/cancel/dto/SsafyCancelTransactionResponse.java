package com.example.mobipay.domain.cancel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SsafyCancelTransactionResponse {

    @JsonProperty("Header")
    private Header header;
    @JsonProperty("REC")
    private Rec rec;

    @Getter
    public static class Header {
        private String responseCode;
        private String responseMessage;
        private String apiName;
        private String transmissionDate;
        private String transmissionTime;
        private String institutionCode;
        private String apiKey;
        private String apiServiceCode;
        private String institutionTransactionUniqueNo;
    }

    @Getter
    public static class Rec {
        private Long transactionUniqueNo;
        private String categoryId;
        private String categoryName;
        private Long merchantId;
        private String merchantName;
        private String transactionDate;
        private String transactionTime;
        private Long transactionBalance;
        private String status;
    }

}
