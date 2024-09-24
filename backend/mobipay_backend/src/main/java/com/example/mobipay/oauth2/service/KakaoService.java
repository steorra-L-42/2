package com.example.mobipay.oauth2.service;

import com.example.mobipay.oauth2.dto.KakaoUserInfoResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class KakaoService {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String ClientId;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String RedirectUri;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String ClientSecret;
    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String TokenUri;

    // 기존 code를 프론트에서 받아 카카오로 code를 쏘아 토큰값을 받는 Dto, 네이티브에선 필요없음
//    public KakaoTokenResponseDto2 getAccessTokenFromKakao2(String code) {
//        RestTemplate restTemplate = new RestTemplate();
//
//        // HTTP 요청 헤더 설정
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        // HTTP 요청 바디 설정
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("grant_type", "authorization_code");
//        body.add("client_id", ClientId);
//        body.add("client_secret", ClientSecret);
//        body.add("redirect_uri", RedirectUri);
//        body.add("code", code);
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
//
//        // 카카오에 엑세스 토큰 요청
//        ResponseEntity<KakaoTokenResponseDto2> response = restTemplate.exchange(
//                TokenUri,
//                HttpMethod.POST,
//                request,
//                KakaoTokenResponseDto2.class
//        );
//        return response.getBody();
//    }


    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserInfoResponseDto> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                request,
                KakaoUserInfoResponseDto.class
        );
        System.out.println(response);

        return response.getBody();  // 사용자 정보 반환
    }

}

