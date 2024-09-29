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
    ACCOUNT_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "수시입출금계좌 상품이 없습니다."),
    CARD_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "카드 상품이 없습니다."),
    NOT_OWNER(HttpStatus.FORBIDDEN, "차주가 아닙니다."),
    NOT_MEMBER(HttpStatus.FORBIDDEN, "멤버가 아닙니다."),
    ALREADY_INVITED(HttpStatus.CONFLICT, "이미 초대된 유저입니다."),
    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 초대입니다."),
    INVITATION_ALREADY_DECIDED(HttpStatus.BAD_REQUEST, "이미 처리된 초대입니다."),
    NOT_APPROVED_OR_REJECTED(HttpStatus.BAD_REQUEST, "승인 또는 거절이 필요합니다."),
    NOT_INVITED(HttpStatus.FORBIDDEN, "초대되지 않은 유저입니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러입니다.");


    private final HttpStatus status;
    private final String message;
}
