package com.example.mobipay.domain.fcmtoken.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class FcmSendDto {

    @NotNull(message = "token 값이 null 입니다.")
    private String token;
    @NotNull(message = "title 값이 null 입니다.")
    private String title;
    @NotNull(message = "body 값이 null 입니다.")
    private String body;
}