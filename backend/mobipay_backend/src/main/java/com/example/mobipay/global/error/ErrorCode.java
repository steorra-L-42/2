package com.example.mobipay.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NOT_NAME_AND_PHONENUMBER(HttpStatus.NOT_FOUND, "사용자 이름과 핸드폰 번호가 필요합니다.");

    private final HttpStatus status;
    private final String message;
}
