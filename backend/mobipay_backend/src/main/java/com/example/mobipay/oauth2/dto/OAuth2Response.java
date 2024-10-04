package com.example.mobipay.oauth2.dto;

public interface OAuth2Response {
    //제공자 (Ex. naver, google, ...)
    String getProvider();

    //제공자에서 발급해주는 아이디(번호)
    String getProviderId();

    //이메일
    String getEmail();

    // 사용자 닉네임
    String getName();

    // 프로필 이미지 URL
    String getPicture();

    // 전화번호
    String getPhoneNumber();
}