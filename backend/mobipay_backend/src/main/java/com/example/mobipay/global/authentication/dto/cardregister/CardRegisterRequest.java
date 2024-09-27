package com.example.mobipay.global.authentication.dto.cardregister;

import com.example.mobipay.global.dto.Header;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CardRegisterRequest {

    private static final String MONDAY = "1";

    @JsonProperty("Header")
    private final Header header;
    private final String cardUniqueNo;
    private final String withdrawalAccountNo;
    private final String withdrawalDate;

    public CardRegisterRequest(String apiName, String apiKey, String userKey, String cardUniqueNo,
                               String withdrawalAccountNo) {
        this.header = Header.of(apiName, apiKey, userKey);
        this.cardUniqueNo = cardUniqueNo;
        this.withdrawalAccountNo = withdrawalAccountNo;
        this.withdrawalDate = MONDAY;
    }
}
