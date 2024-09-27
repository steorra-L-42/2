package com.example.mobipay.global.authentication.dto.ssafyusercheck;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SsafyUserCheckRequest {

    private String userId;
    private String apiKey;
}
