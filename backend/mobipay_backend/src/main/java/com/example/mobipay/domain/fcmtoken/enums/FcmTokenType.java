package com.example.mobipay.domain.fcmtoken.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FcmTokenType {
    AUTO_PAY_FAILED("autoPayFailed"),
    TRANSACTION_REQUEST("transactionRequest"),
    TRANSACTION_RESULT("transactionResult"),
    INVITATION("invitation"),
    TRANSACTION_CANCEL("transactionCancel");

    private final String value;
}
