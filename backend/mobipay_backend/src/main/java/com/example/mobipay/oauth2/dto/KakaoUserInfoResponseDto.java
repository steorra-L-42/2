package com.example.mobipay.oauth2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfoResponseDto {

    // 회원 번호
    @JsonProperty("id")
    private Long userId;

    // 사용자 프로퍼티
    @JsonProperty("properties")
    private HashMap<String, String> properties;

    // 카카오 계정 정보
    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoAccount {

        // 프로필 정보 제공 동의 여부
        @JsonProperty("profile_needs_agreement")
        private Boolean isProfileAgree;

        // 프로필 정보
        @JsonProperty("profile")
        private Profile profile;

        // 이름 제공 동의 여부
        @JsonProperty("name_needs_agreement")
        private Boolean isNameAgree;

        // 사용자 이름
        @JsonProperty("name")
        private String name;

        // 이메일 제공 동의 여부
        @JsonProperty("email_needs_agreement")
        private Boolean isEmailAgree;

        // 이메일
        @JsonProperty("email")
        private String email;

        // 전화번호 제공 동의 여부
        @JsonProperty("phone_number_needs_agreement")
        private Boolean isPhoneNumberAgree;

        // 전화번호
        @JsonProperty("phone_number")
        private String phoneNumber;

        @Getter
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Profile {

            // 닉네임
            @JsonProperty("nickname")
            private String nickName;

            // 프로필 이미지 URL
            @JsonProperty("profile_image_url")
            private String picture;

            // 썸네일 이미지 URL
            @JsonProperty("thumbnail_image_url")
            private String thumbnailImageUrl;

            // 기본 이미지 여부
            @JsonProperty("is_default_image")
            private Boolean isDefaultImage;
        }
    }
}
