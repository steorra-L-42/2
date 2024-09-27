package com.example.mobipay.oauth2.controller;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
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
//            String name = kakaoUserInfoResponseDto.getKakaoAccount().getName();
            String name = "noname";
//            String phoneNumber = kakaoUserInfoResponseDto.getKakaoAccount().getPhoneNumber();
            String phoneNumber = "nophone";
            String picture = kakaoUserInfoResponseDto.getKakaoAccount().getProfile().getPicture();

            // 사용자의 이메일로 사용자 조회 또는 회원가입 처리
//            MobiUser mobiUser = userService.findOrCreateUser(email, name, phoneNumber, picture);
//            System.out.println("이거정답" + mobiUser.getEmail());
//            System.out.println("이거정답" + mobiUser.getName());
//            System.out.println("이거정답" + mobiUser.getPhoneNumber());
//            System.out.println("이거정답" + mobiUser.getPicture());

            Boolean existEmail = userService.checkEmailInMobipay(email);

            if (!existEmail) {
                // ssafy api 추가
                return userService.sendUserDetailRequest(email, picture);
            } else {
                // existEmail이 true면 이미 가입된 유저이므로
                MobiUser mobiUser = mobiUserRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
                System.out.println("이거정답" + mobiUser.getEmail());
                System.out.println("이거정답" + mobiUser.getName());
                System.out.println("이거정답" + mobiUser.getPhoneNumber());
                System.out.println("이거정답" + mobiUser.getPicture());

                kakaoTokenService.saveOrUpdateKakaoToken(accessToken, refreshToken, mobiUser);

                String jwtaccessToken = userService.generateJwtAccessToken(mobiUser);
                Cookie jwtrefreshToken = userService.generateJwtRefreshToken(mobiUser);
                System.out.println("accessToken : " + accessToken);
                System.out.println("refreshToken : " + refreshToken);

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

        MobiUser mobiUser = MobiUser.builder()
                .email(email)
                .name(name)
                .phoneNumber(phoneNumber)
                .picture(picture)
                .build();

        userService.createUser(email, name, phoneNumber, picture);

        String jwtaccessToken = userService.generateJwtAccessToken(mobiUser);
        Cookie jwtrefreshToken = userService.generateJwtRefreshToken(mobiUser);

        System.out.println("로그인 컨트롤러 jwt ref 토큰" + jwtrefreshToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtaccessToken);  // 헤더에 JWT 토큰 추가

        return ResponseEntity.ok().headers(headers).build();
    }
}
