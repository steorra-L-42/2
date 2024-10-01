package com.example.mobipay.global.authentication.dto.accountdepositupdate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AccountDepositUpdateResponse {

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
        private String transactionDate;
    }
}
