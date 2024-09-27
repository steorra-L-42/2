package com.example.mobipay.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    DUPLICATED_CAR_NUMBER(HttpStatus.CONFLICT, "중복된 차량 번호입니다."),
    MOBI_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    CAR_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 차량입니다."),
    NOT_OWNER(HttpStatus.FORBIDDEN, "차주가 아닙니다."),
    ACCOUNT_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "수시입출금계좌 상품이 없습니다."),
    CARD_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "카드 상품이 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러입니다.");


    private final HttpStatus status;
    private final String message;
}
