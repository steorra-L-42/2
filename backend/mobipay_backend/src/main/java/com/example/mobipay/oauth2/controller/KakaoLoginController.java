package com.example.mobipay.oauth2.controller;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.global.authentication.service.SignUpServiceImpl;
import com.example.mobipay.oauth2.dto.KakaoTokenResponseDto;
import com.example.mobipay.oauth2.dto.KakaoUserInfoResponseDto;
import com.example.mobipay.oauth2.dto.UserRequestDto;
import com.example.mobipay.oauth2.service.KakaoService;
import com.example.mobipay.oauth2.service.KakaoTokenService;
import com.example.mobipay.oauth2.service.RefreshTokenService;
import com.example.mobipay.oauth2.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
    private final MobiUserRepository mobiUserRepository;
    private final SignUpServiceImpl signUpServiceImpl;
    private final RefreshTokenService refreshTokenService;


    @PostMapping("/login")
    public ResponseEntity<?> receiveKakaoToken(@Valid @RequestBody KakaoTokenResponseDto kakaoTokenResponseDto) {
        try {
            // 액세스, 리프레시 토큰 받기
            String accessToken = kakaoTokenResponseDto.getAccessToken();
            String refreshToken = kakaoTokenResponseDto.getRefreshToken();

            //카카오 사용자 정보 api
            KakaoUserInfoResponseDto kakaoUserInfoResponseDto = kakaoService.getUserInfo(accessToken);
            String email = kakaoUserInfoResponseDto.getKakaoAccount().getEmail();
            String name = "noname";
            String phoneNumber = "nophone";
            String picture = kakaoUserInfoResponseDto.getKakaoAccount().getProfile().getPicture();

            Boolean existEmail = userService.checkEmailInMobipay(email);

            if (!existEmail) {
                return userService.sendUserDetailRequest(email, picture);
            } else {
                // existEmail이 true면 이미 가입된 유저이므로
                MobiUser mobiUser = mobiUserRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

                kakaoTokenService.saveOrUpdateKakaoToken(accessToken, refreshToken, mobiUser);

                String jwtaccessToken = userService.generateJwtAccessToken(mobiUser);
                Cookie jwtrefreshToken = userService.generateJwtRefreshToken(mobiUser);

                //

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + jwtaccessToken);  // 헤더에 JWT 토큰 추가

                return ResponseEntity.ok().headers(headers).build();
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during login process: " + e.getMessage());
        }

    }


    @PostMapping("/detail")
    public ResponseEntity<String> requestUserDetails(@RequestBody UserRequestDto userRequestDto) {
        String email = userRequestDto.getEmail();
        String name = userRequestDto.getName();
        String phoneNumber = userRequestDto.getPhoneNumber();
        String picture = userRequestDto.getPicture();
        String accessToken = userRequestDto.getAccessToken();
        String refreshToken = userRequestDto.getRefreshToken();
        // 여기서 토큰은 유저의 카카오 Token, DB에 저장하기 위해 다시 값을 받음

        // ssafy api 조회
        signUpServiceImpl.signUp(email, name, phoneNumber, picture);

        MobiUser mobiUser = userService.createUser(email, name, phoneNumber, picture);

        kakaoTokenService.saveOrUpdateKakaoToken(accessToken, refreshToken, mobiUser);

        String jwtaccessToken = userService.generateJwtAccessToken(mobiUser);
        Cookie jwtrefreshToken = userService.generateJwtRefreshToken(mobiUser);

        System.out.println("로그인 컨트롤러 jwt ref 토큰" + jwtrefreshToken);

        // mobi db에 user 저장
        // ssafy db에 user 저장

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtaccessToken);  // 헤더에 JWT 토큰 추가

        return ResponseEntity.ok().headers(headers).build();
    }
}
