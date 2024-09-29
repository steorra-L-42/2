package com.example.mobipay.oauth2.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenType {
    BEARER("Bearer "),
    ACCESS("Authorization", 24 * 60 * 60),
    REFRESH("refresh", 7 * 24 * 60 * 60);

    private final String type;
    private final Integer expiration;

    TokenType(String type) {
        this(type, null);
    }
}
