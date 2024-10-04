package com.example.mobipay.oauth2.controller;

import static com.example.mobipay.oauth2.enums.TokenType.ACCESS;
import static com.example.mobipay.oauth2.enums.TokenType.BEARER;

import com.example.mobipay.domain.kakaotoken.service.KakaoTokenService;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.error.MobiUserNotFoundException;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.global.authentication.service.SignUpServiceImpl;
import com.example.mobipay.oauth2.dto.KakaoTokenResponseDto;
import com.example.mobipay.oauth2.dto.KakaoUserInfoResponseDto;
import com.example.mobipay.oauth2.dto.UserRequestDto;
import com.example.mobipay.oauth2.dto.UserResponseDto;
import com.example.mobipay.oauth2.service.UserService;
import com.example.mobipay.oauth2.util.CookieMethods;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class KakaoLoginController {

    private final UserService userService;
    private final KakaoTokenService kakaoTokenService;
    private final MobiUserRepository mobiUserRepository;
    private final SignUpServiceImpl signUpServiceImpl;
    private final CookieMethods cookieMethods;

    @Value("${KAKAO_USER_INFO_URI}")
    private String UserInfoUri;


    @PostMapping("/login")
    public ResponseEntity<?> receiveKakaoToken(@Valid @RequestBody KakaoTokenResponseDto kakaoTokenResponseDto) {
        try {
            // 액세스, 리프레시 토큰 받기
            String accessToken = kakaoTokenResponseDto.getAccessToken();
            String refreshToken = kakaoTokenResponseDto.getRefreshToken();

            //카카오 사용자 정보 api
            KakaoUserInfoResponseDto kakaoUserInfoResponseDto = getUserInfo(accessToken);
            String email = kakaoUserInfoResponseDto.getKakaoAccount().getEmail();
            String picture = kakaoUserInfoResponseDto.getKakaoAccount().getProfile().getPicture();

            Boolean existEmail = userService.checkEmailInMobipay(email);

            if (!existEmail) {
                return sendUserDetailRequest(email, picture);

            }
            // existEmail이 true면 이미 가입된 유저이므로
            MobiUser mobiUser = mobiUserRepository.findByEmail(email)
                    .orElseThrow(MobiUserNotFoundException::new);

            kakaoTokenService.saveOrUpdateKakaoToken(accessToken, refreshToken, mobiUser);

            String jwtAccessToken = userService.generateJwtAccessToken(mobiUser);

            // 헤더 생성 후, 헤더에 JWT 토큰 추가
            HttpHeaders headers = new HttpHeaders();
            headers.add(ACCESS.getType(), BEARER.getType() + jwtAccessToken);

            UserResponseDto responseBody = userService.getUserDetail(email, mobiUser.getName(),
                    mobiUser.getPhoneNumber(), jwtAccessToken);

            return ResponseEntity.ok().headers(headers).body(responseBody);


        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during login process: " + e.getMessage());
        }
    }


    @PostMapping("/detail")
    public ResponseEntity<UserResponseDto> requestUserDetails(@RequestBody UserRequestDto userRequestDto,
                                                              HttpServletResponse response) {
        // 여기서 토큰은 유저의 카카오 Token, DB에 저장하기 위해 다시 값을 받음
        String email = userRequestDto.getEmail();
        String name = userRequestDto.getName();
        String phoneNumber = userRequestDto.getPhoneNumber();
        String picture = userRequestDto.getPicture();
        String accessToken = userRequestDto.getAccessToken();
        String refreshToken = userRequestDto.getRefreshToken();
        // ssafy api 조회
        MobiUser mobiUser = signUpServiceImpl.signUp(email, name, phoneNumber, picture);

        userService.createUser(email, name, phoneNumber, picture);

        kakaoTokenService.saveOrUpdateKakaoToken(accessToken, refreshToken, mobiUser);

        String jwtAccessToken = userService.generateJwtAccessToken(mobiUser);
        Cookie jwtRefreshToken = userService.generateJwtRefreshToken(mobiUser);
        response.addCookie(jwtRefreshToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add(ACCESS.getType(), BEARER.getType() + jwtAccessToken);  // 헤더에 JWT 토큰 추가

        UserResponseDto responseBody = userService.getUserDetail(email, name, phoneNumber, jwtAccessToken);

        headers.add(HttpHeaders.SET_COOKIE, jwtRefreshToken.toString());

        return ResponseEntity.ok().headers(headers).body(responseBody);
    }

    public ResponseEntity<String> sendUserDetailRequest(String email, String picture) {
        // 안드로이드에 name과 phoneNumber를 요청하는 HTTP 404 응답을 반환
        String responseMessage = "Please provide name, phone number. Email: " + email + ", Picture: " + picture;
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
    }

    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        RestClient client = RestClient.builder()
                .baseUrl(UserInfoUri)  // 기본 URL 설정
                .build();

        try {
            KakaoUserInfoResponseDto response = client.get()
                    .uri("/") // 특정 경로를 추가로 설정하지 않으면 기본 URL을 사용
                    .header(ACCESS.getType(), BEARER.getType() + accessToken) // 헤더 추가
                    .retrieve()  // 요청 수행
                    .body(KakaoUserInfoResponseDto.class);  // 응답 바디를 원하는 DTO로 매핑

            return response;  // 사용자 정보 반환

        } catch (RestClientResponseException ex) {
            // 예외 처리 (예: API 호출 실패 등)
            throw new RuntimeException("Failed to fetch user info from Kakao API", ex);
        }
    }
}