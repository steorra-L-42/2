package com.example.mobipay.oauth2.service;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.domain.refreshtoken.entity.RefreshToken;
import com.example.mobipay.oauth2.jwt.JWTUtil;
import com.example.mobipay.oauth2.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final MobiUserRepository mobiUserRepository;
    private final JWTUtil jwtUtil;

    @Transactional
    public void addRefreshToken(MobiUser mobiUser, String value) {
        RefreshToken refreshToken = getRefreshToken(value);

        System.out.println("getRefreshTOken" + mobiUser.getRefreshToken());
        if (mobiUser.getRefreshToken() != null) {
            revokeExistingRefreshToken(mobiUser);
        }

        if (mobiUser.getRefreshToken() == null) {
            refreshToken = createRefreshToken(value);
            mobiUser.addRefreshToken(refreshToken);
        }
        System.out.println("리프레시토큰서비스" + refreshToken.getValue());

        saveRefreshTokenAndUser(mobiUser, refreshToken);
    }

    private RefreshToken getRefreshToken(String value) {
        return refreshTokenRepository.findByValue(value);
    }

    private RefreshToken createRefreshToken(String value) {
        LocalDateTime issuedAt = jwtUtil.getIssuedAt(value);
        LocalDateTime expiredAt = jwtUtil.getExpiredAt(value);

        return RefreshToken.builder()
                .value(value)
                .issuedAt(issuedAt)
                .expiredAt(expiredAt)
                .build();
    }

    private void revokeExistingRefreshToken(MobiUser mobiUser) {
        refreshTokenRepository.revokeById(mobiUser.getRefreshToken().getId());
        mobiUser.deleteRefreshToken();
    }

    private void saveRefreshTokenAndUser(MobiUser mobiuser, RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
        mobiUserRepository.save(mobiuser);
    }

}
