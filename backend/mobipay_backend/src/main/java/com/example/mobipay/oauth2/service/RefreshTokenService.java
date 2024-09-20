package com.example.mobipay.oauth2.service;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.refreshtoken.entity.RefreshToken;
import com.example.mobipay.oauth2.jwt.JWTUtil;
import com.example.mobipay.oauth2.repository.MobiUserRepository;
import com.example.mobipay.oauth2.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final MobiUserRepository mobiUserRepository;
    private final JWTUtil jwtUtil;

    @Transactional
    public Void addRefreshToken(MobiUser mobiUser, String value) {
        RefreshToken refreshToken = getRefreshToken(value);

        if (mobiUser.getRefreshToken() != null) {
            revokeExistingRefreshToken(mobiUser);
        }

        if (mobiUser.getRefreshToken() == null) {
            refreshToken = createRefreshToken(value);
            mobiUser.addRefreshToken(refreshToken);
        }

    }

    private RefreshToken getRefreshToken(String value) {
        return refreshTokenRepository.findByValue(value);
    }

    private void revokeExistingRefreshToken(MobiUser mobiUser) {
        refreshTokenRepository.revokeById(mobiUser.getRefreshToken().getId());
        mobiUser.deleteRefreshToken();
    }


}
