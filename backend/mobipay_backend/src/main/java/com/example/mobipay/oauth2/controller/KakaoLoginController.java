package com.example.mobipay.oauth2.controller;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.refreshtoken.entity.RefreshToken;
import com.example.mobipay.oauth2.dto.JwtResponseDto;
import com.example.mobipay.oauth2.dto.KakaoTokenResponseDto;
import com.example.mobipay.oauth2.dto.KakaoUserInfoResponseDto;
import com.example.mobipay.oauth2.service.KakaoService;
import com.example.mobipay.oauth2.service.UserService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class KakaoLoginController {

    private final KakaoService kakaoService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> kakaoLogin(@RequestParam("code") String code) {
        try {
            System.out.println("aa" + code);
            // 카카오 서버로 인가 코드를 보내서 액세스 토큰과 리프레시 토큰 받기
            KakaoTokenResponseDto tokenResponse = kakaoService.getAccessTokenFromKakao(code);

            // 액세스 토큰을 사용해 카카오 사용자 정보를 가져옴
            KakaoUserInfoResponseDto kakaoUserInfoResponseDto = kakaoService.getUserInfo(
                    tokenResponse.getAccessToken());

            String email = kakaoUserInfoResponseDto.getKakaoAccount().getEmail();
            String name = kakaoUserInfoResponseDto.getKakaoAccount().getProfile().getNickName();
            String phoneNumber = kakaoUserInfoResponseDto.getKakaoAccount().getPhoneNumber();
            String picture = kakaoUserInfoResponseDto.getKakaoAccount().getProfile().getProfileImageUrl();

            // 사용자의 이메일로 사용자 조회 또는 회원가입 처리
            MobiUser mobiUser = userService.findOrCreateUser(email, name, phoneNumber, picture);

            // 리프레시 토큰값 추가
            RefreshToken refreshToken = new RefreshToken(
                    tokenResponse.getRefreshToken(),  // 리프레시 토큰 값
                    LocalDateTime.now(),  // 발급 시간
                    LocalDateTime.now().plusDays(30)
            );

            // 리프레시 토큰 추가
            userService.addRefreshToken(mobiUser.getId(), refreshToken.getValue(), refreshToken.getIssuedAt(),
                    refreshToken.getExpiredAt());

            //JWT 토큰 발급 후 클라이언트에 반환
            String jwtToken = userService.generateJwtToken(mobiUser);
            return ResponseEntity.ok(
                    new JwtResponseDto(jwtToken, LocalDateTime.now(), LocalDateTime.now().plusDays(30), false));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
//        // 토큰 정보를 클라이언트에 반환하거나, JWT로 변환해서 반환할 수 있음
//        return ResponseEntity.ok(KakaoTokenResponseDto);
    }
}