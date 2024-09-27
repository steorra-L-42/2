package com.example.mobipay.global.authentication.dto.accountcheck;

import com.example.mobipay.global.authentication.dto.AccountRec;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class AccountCheckResponse {

    @JsonProperty("Header")
    private Header header;
    @JsonProperty("REC")
    private List<Rec> recs;

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
        private String bankName;
        private String userName;
        private String accountNo;
        private String accountName;
        private String accountTypeCode;
        private String accountTypeName;
        private String accountCreatedDate;
        private String accountExpiryDate;
        private String dailyTransferLimit;
        private String oneTimeTransferLimit;
        private String accountBalance;
        private String lastTransactionDate;
        private String currency;
    }
}
