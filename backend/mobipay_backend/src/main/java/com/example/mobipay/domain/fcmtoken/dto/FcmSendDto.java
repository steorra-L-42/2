package com.example.mobipay.domain.fcmtoken.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
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
    private Map<String, String> data;

    public FcmSendDto(String token, String title, String body) {
        this.token = token;
        this.title = title;
        this.body = body;
        this.data = null;
    }
}