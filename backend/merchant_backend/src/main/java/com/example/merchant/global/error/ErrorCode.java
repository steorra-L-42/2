package com.example.merchant.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    DUPLICATEDPARKING(HttpStatus.BAD_REQUEST, "중복된 주차가 존재합니다."),
    INVALIDMERAPIKEY(HttpStatus.UNAUTHORIZED , "잘못된 merApiKey 입니다.");

    private final HttpStatus status;
    private final String message;
}
