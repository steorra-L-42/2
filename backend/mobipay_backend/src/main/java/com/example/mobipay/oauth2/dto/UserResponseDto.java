package com.example.mobipay.oauth2.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {
    private String email;
    private String name;
    private String phoneNumber;
    private String jwtAccessToken;
}