package com.example.mobipay.formsignup.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FormSignUpResponse {

    private static final String ALREADY_EXIST_USER = "이미 가입한 유저입니다.";
    private static final String FORM_SIGNUP_SUCCESS = "회원 가입이 성공했습니다.";

    private String message;

    public static FormSignUpResponse success() {
        return FormSignUpResponse.builder()
                .message(FORM_SIGNUP_SUCCESS)
                .build();
    }

    public static FormSignUpResponse userAlreadyExists() {
        return FormSignUpResponse.builder()
                .message(ALREADY_EXIST_USER)
                .build();
    }
}
