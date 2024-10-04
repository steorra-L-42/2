package com.example.mobipay.domain.refreshtoken.service;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.domain.refreshtoken.entity.RefreshToken;
import com.example.mobipay.domain.refreshtoken.repository.RefreshTokenRepository;
import com.example.mobipay.oauth2.jwt.JWTUtil;
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

        if (mobiUser.getRefreshToken() != null) {
            revokeExistingRefreshToken(mobiUser);
        }

        if (mobiUser.getRefreshToken() == null) {
            refreshToken = createRefreshToken(value);
            mobiUser.addRefreshToken(refreshToken);
        }
        refreshTokenRepository.save(refreshToken);
        mobiUserRepository.save(mobiUser);
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
}
