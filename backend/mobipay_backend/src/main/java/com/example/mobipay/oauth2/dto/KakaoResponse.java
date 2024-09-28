package com.example.mobipay.oauth2.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attributes;
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> properties;
//    private final String refreshToken;

    // 생성자에서 카카오 응답을 처리하는 방식 수정
    public KakaoResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.properties = (Map<String, Object>) attributes.get("properties");
//        this.refreshToken = (Map<String, Object>) attributes.get("properties");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        if (kakaoAccount != null && kakaoAccount.containsKey("email")) {
            return kakaoAccount.get("email").toString();
        }
        return null;
    }

    @Override
    public String getName() {
        if (properties != null && properties.containsKey("nickname")) {
            return properties.get("nickname").toString();
        }
        return null;
    }

    @Override
    public String getPicture() {
        if (properties != null && properties.containsKey("profile_image")) {
            return properties.get("profile_image").toString();
        }
        return null;
    }

    @Override
    public String getPhoneNumber() {
        if (kakaoAccount != null && kakaoAccount.containsKey("phone_number")) {
            return kakaoAccount.get("phone_number").toString();
        }
        return null;
    }

}
