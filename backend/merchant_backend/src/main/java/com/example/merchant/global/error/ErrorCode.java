package com.example.merchant.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    MULTIPLE_NOT_PAID(HttpStatus.BAD_REQUEST, "중복된 미납 주차가 존재합니다."),
    DUPLICATED_PARKING(HttpStatus.BAD_REQUEST, "중복된 주차가 존재합니다."),
    INVALID_MER_API_KEY(HttpStatus.UNAUTHORIZED , "잘못된 merApiKey 입니다."),
    NOT_EXIST_PARKING(HttpStatus.NOT_FOUND , "주차된 적 없는 차량이 입니다."),
    WEBSOCKET_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "웹소켓 에러가 발생했습니다."),
    UNKNOWN_MERCHANTID(HttpStatus.BAD_REQUEST, "알 수 없는 Merchant Id 입니다."),
    INVALID_TRANSACTION_UNIQUE_NO(HttpStatus.BAD_REQUEST, "유효하지 않은 거래 고유 번호입니다."),
    INVALID_MERCHANT_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 Merchant Type 입니다."),
    INVALID_CAR_NUMBER(HttpStatus.BAD_REQUEST, "차량 번호는 7자리 이상 8자리 이하로 입력해주세요.");

    private final HttpStatus status;
    private final String message;
}
