package com.example.mobipay.global.authentication.dto.accountregister;

import com.example.mobipay.global.authentication.dto.AccountRec;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AccountRegisterResponse {

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
    public static class Rec implements AccountRec {
        private String bankCode;
        private String accountNo;
        private Currency currency;
    }

    @Getter
    public static class Currency {
        private String currency;
        private String currencyName;
    }
}
