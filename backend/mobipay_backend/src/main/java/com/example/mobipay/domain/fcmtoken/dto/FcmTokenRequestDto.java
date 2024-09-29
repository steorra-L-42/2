package com.example.mobipay.domain.fcmtoken.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class FcmTokenRequestDto {

    @NotNull(message = "token 값이 null 입니다.")
    private String token;
}
