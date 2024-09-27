package com.example.mobipay.global.authentication.dto.accountregister;

import com.example.mobipay.global.dto.Header;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AccountRegisterRequest {

    @JsonProperty("Header")
    private final Header header;
    private final String accountTypeUniqueNo;

    public AccountRegisterRequest(String apiName, String apiKey, String userKey, String accountTypeUniqueNo) {
        this.header = Header.of(apiName, apiKey, userKey);
        this.accountTypeUniqueNo = accountTypeUniqueNo;
    }
}
