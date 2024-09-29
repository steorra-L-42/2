package com.example.mobipay.oauth2.service;

import static com.example.mobipay.oauth2.enums.TokenType.REFRESH;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.domain.refreshtoken.entity.repository.RefreshTokenRepository;
import com.example.mobipay.oauth2.jwt.JWTUtil;
import com.example.mobipay.oauth2.util.CookieMethods;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
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
}
