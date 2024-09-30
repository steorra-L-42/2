package com.example.merchant.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    MULTIPLENOTPAID(HttpStatus.BAD_REQUEST, "중복된 미납 주차가 존재합니다."),
    DUPLICATEDPARKING(HttpStatus.BAD_REQUEST, "중복된 주차가 존재합니다."),
    INVALIDMERAPIKEY(HttpStatus.UNAUTHORIZED , "잘못된 merApiKey 입니다."),
    NOTEXISTPARKING(HttpStatus.NOT_FOUND , "주차된 적 없는 차량이 입니다."),
    WEBSOCKETERROR(HttpStatus.INTERNAL_SERVER_ERROR, "웹소켓 에러가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
