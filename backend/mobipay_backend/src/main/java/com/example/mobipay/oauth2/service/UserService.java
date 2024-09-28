package com.example.mobipay.oauth2.service;

import static com.example.mobipay.oauth2.enums.TokenType.REFRESH;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.oauth2.jwt.JWTUtil;
import com.example.mobipay.oauth2.repository.RefreshTokenRepository;
import com.example.mobipay.oauth2.util.CookieMethods;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MobiUserRepository mobiUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final CookieMethods cookieMethods;

    public boolean checkEmailInMobipay(String email) {
        return mobiUserRepository.existsByEmail(email);
    }

    // 2. 사용자 생성 (카카오 API에서 받아온 정보 기반)
    public MobiUser createUser(String email, String name, String phoneNumber, String picture) {

        MobiUser mobiUser = MobiUser.builder()
                .email(email)
                .name(name)
                .phoneNumber(phoneNumber)
                .picture(picture)
                .build();
        return mobiUser;
    }


    public String generateJwtAccessToken(MobiUser mobiUser) {
        return jwtUtil.createAccessToken(
                mobiUser.getEmail(),
                mobiUser.getName(),
                mobiUser.getPicture(),
                mobiUser.getPhoneNumber()
        );
    }


    public Cookie generateJwtRefreshToken(MobiUser mobiUser) {
        String email = mobiUser.getEmail();
        String name = mobiUser.getName();
        String picture = mobiUser.getPicture();
        String phoneNumber = mobiUser.getPhoneNumber();

        String refreshToken = jwtUtil.createRefreshToken(email, name, phoneNumber, picture);
        refreshTokenService.addRefreshToken(mobiUser, refreshToken);

        return cookieMethods.createCookie(REFRESH.getType(), refreshToken);
    }

//    // 5. 리프레시 토큰 삭제
//    public void deleteRefreshToken(Long userId) {
//        MobiUser mobiUser = mobiUserRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
//        mobiUser.deleteRefreshToken();
//    }

    public ResponseEntity<String> sendUserDetailRequest(String email, String picture) {
        // 안드로이드에 name과 phoneNumber를 요청하는 HTTP 404 응답을 반환
        String responseMessage = "Please provide name, phone number. Email: " + email + ", Picture: " + picture;
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseMessage);
    }
}
