package com.example.mobipay.domain.fcmtoken.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FcmTokenResponseDto {

    private static final String FCM_TOKEN_SAVE_SUCCESS = "FCM 토큰이 성공적으로 저장되었습니다.";

    private String message;

    public static FcmTokenResponseDto success() {
        return FcmTokenResponseDto.builder()
                .message(FCM_TOKEN_SAVE_SUCCESS)
                .build();
    }
}
