package com.example.mobipay.global.authentication.dto.cardcheck;

import com.example.mobipay.global.dto.Header;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CardCheckRequest {

    @JsonProperty("Header")
    private final Header header;

    public CardCheckRequest(String apiName, String apiKey, String userKey) {
        this.header = Header.of(apiName, apiKey, userKey);
    }
}
