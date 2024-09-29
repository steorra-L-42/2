package com.example.mobipay.oauth2.dto;

import lombok.Getter;

@Getter
public class UserRequestDto {
    private String email;
    private String name;
    private String phoneNumber;
    private String picture;
    private String accessToken;
    private String refreshToken;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
