package com.example.mobipay.oauth2.controller;

import static com.example.mobipay.oauth2.enums.TokenType.ACCESS;
import static com.example.mobipay.oauth2.enums.TokenType.BEARER;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.error.MobiUserNotFoundException;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.global.authentication.service.SignUpServiceImpl;
import com.example.mobipay.oauth2.dto.KakaoTokenResponseDto;
import com.example.mobipay.oauth2.dto.KakaoUserInfoResponseDto;
import com.example.mobipay.oauth2.dto.UserRequestDto;
import com.example.mobipay.oauth2.service.KakaoTokenService;
import com.example.mobipay.oauth2.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class KakaoLoginController {

    private final UserService userService;
    private final KakaoTokenService kakaoTokenService;
    private final MobiUserRepository mobiUserRepository;
    private final SignUpServiceImpl signUpServiceImpl;

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

            String jwtaccessToken = userService.generateJwtAccessToken(mobiUser);

            // 헤더 생성 후, 헤더에 JWT 토큰 추가
            HttpHeaders headers = new HttpHeaders();
            headers.add(ACCESS.getType(), BEARER.getType() + jwtaccessToken);

            return ResponseEntity.ok().headers(headers).build();


        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during login process: " + e.getMessage());
        }
    }


    @PostMapping("/detail")
    public ResponseEntity<String> requestUserDetails(@RequestBody UserRequestDto userRequestDto) {
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

        String jwtaccessToken = userService.generateJwtAccessToken(mobiUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add(ACCESS.getType(), BEARER.getType() + jwtaccessToken);  // 헤더에 JWT 토큰 추가

        return ResponseEntity.ok().headers(headers).build();
    }

    public ResponseEntity<String> sendUserDetailRequest(String email, String picture) {
        // 안드로이드에 name과 phoneNumber를 요청하는 HTTP 404 응답을 반환
        String responseMessage = "Please provide name, phone number. Email: " + email + ", Picture: " + picture;
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
    }

    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        // 추후에 RestClient 라고 더 좋은게 있는데 리팩토링 고려 해볼 것
        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add(ACCESS.getType(), BEARER.getType() + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<KakaoUserInfoResponseDto> response = restTemplate.exchange(
                UserInfoUri,
                HttpMethod.GET,
                request,
                KakaoUserInfoResponseDto.class
        );
        return response.getBody();  // 사용자 정보 반환
    }
}
