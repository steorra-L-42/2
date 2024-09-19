package com.example.mobipay.oauth2.dto;

public interface OAuth2Response {
    //제공자 (Ex. naver, google, ...)
    String getProvider();

    //제공자에서 발급해주는 아이디(번호)
    String getProviderId();

    //이메일
    String getEmail();

    // 사용자 닉네임
    String getname();

    // 프로필 이미지 URL
    String getPicture();

    // 전화번호
    String getPhoneNumber();
}

//종합 정리: Kakao OAuth2를 통해 받을 수 있는 주요 사용자 정보
//Provider (제공자): kakao
//ProviderId (사용자 고유 ID): id
//Email: kakao_account.email
//Name (닉네임): kakao_account.profile.nickname
//Profile Image: kakao_account.profile.profile_image_url
//Gender: kakao_account.gender
//Birthdate: kakao_account.birthday
//Birthyear: kakao_account.birthyear
//Age Range: kakao_account.age_range
//Phone Number: kakao_account.phone_number
//CI (Connecting Information): kakao_account.ci
//Connected At: connected_at