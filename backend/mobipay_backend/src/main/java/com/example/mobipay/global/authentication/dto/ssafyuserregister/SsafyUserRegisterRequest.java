package com.example.mobipay.global.authentication.dto.ssafyuserregister;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SsafyUserRegisterRequest {

    private String userId;
    private String apiKey;
}
