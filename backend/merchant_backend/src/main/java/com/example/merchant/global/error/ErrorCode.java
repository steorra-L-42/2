package com.example.merchant.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    ;
//     example of error code
//    NOT_OWNER(HttpStatus.FORBIDDEN, "차주가 아닙니다.");

    private final HttpStatus status;
    private final String message;
}
