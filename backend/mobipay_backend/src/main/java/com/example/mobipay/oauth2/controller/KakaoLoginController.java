package com.example.mobipay.oauth2.controller;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.oauth2.dto.KakaoTokenResponseDto;
import com.example.mobipay.oauth2.dto.KakaoUserInfoResponseDto;
import com.example.mobipay.oauth2.service.KakaoService;
import com.example.mobipay.oauth2.service.KakaoTokenService;
import com.example.mobipay.oauth2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class KakaoLoginController {

    private final KakaoService kakaoService;
    private final UserService userService;
    private final KakaoTokenService kakaoTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> receiveKakaoToken(@RequestBody KakaoTokenResponseDto kakaoTokenResponseDto) {
        try {
            // 액세스, 리프레시 토큰 받기
            String accessToken = kakaoTokenResponseDto.getAccessToken();
            String refreshToken = kakaoTokenResponseDto.getRefreshToken();

            //카카오 사용자 정보 api
            KakaoUserInfoResponseDto kakaoUserInfoResponseDto = kakaoService.getUserInfo(accessToken);
            String email = kakaoUserInfoResponseDto.getKakaoAccount().getEmail();
            String name = kakaoUserInfoResponseDto.getKakaoAccount().getName();
            String phoneNumber = kakaoUserInfoResponseDto.getKakaoAccount().getPhoneNumber();
            String picture = kakaoUserInfoResponseDto.getKakaoAccount().getProfile().getPicture();

            // 사용자의 이메일로 사용자 조회 또는 회원가입 처리
            MobiUser mobiUser = userService.findOrCreateUser(email, name, phoneNumber, picture);

            kakaoTokenService.saveOrUpdateKakaoToken(accessToken, refreshToken, mobiUser);

            String jwtaccessToken = userService.generateJwtAccessToken(mobiUser);
            String jwtrefreshToken = userService.generateJwtRefreshToken(mobiUser);
            System.out.println("accessToken : " + accessToken);
            System.out.println("refreshToken : " + refreshToken);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + jwtaccessToken);  // 헤더에 JWT 토큰 추가

            // 리프레시 토큰을 쿠키로 추가
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh", jwtrefreshToken)
                    .httpOnly(true)  // JS에서 접근 불가
                    .secure(false)    // HTTPS에서만 전송 (개발 환경에서 비활성화 가능)
                    .path("/")       // 쿠키 경로 설정
                    .maxAge(7 * 24 * 60 * 60)  // 쿠키 만료 시간 설정 (1주일)
                    .sameSite("Strict")  // SameSite 설정 (Strict, Lax, None)
                    .build();

            headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());  // 헤더에 쿠키 추가

            return ResponseEntity.ok().headers(headers).build();


        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during login process: " + e.getMessage());
        }

    }
}
