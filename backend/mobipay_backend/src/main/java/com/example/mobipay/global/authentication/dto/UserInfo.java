package com.example.mobipay.global.authentication.dto;

import lombok.Getter;

@Getter
public class UserInfo {

    private final String email;
    private final String name;
    private final String phoneNumber;
    private final String picture;

    private UserInfo(String email, String name, String phoneNumber, String picture) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.picture = picture;
    }

    public static UserInfo of(String email, String name, String phoneNumber, String picture) {
        return new UserInfo(email, name, phoneNumber, picture);
    }
}
