package com.example.mobipay.domain.kakaotoken.service;

import com.example.mobipay.domain.kakaotoken.entity.KakaoToken;
import com.example.mobipay.domain.kakaotoken.repository.KakaoTokenRepository;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KakaoTokenService {

    private final KakaoTokenRepository kakaoTokenRepository;

    @Transactional
    public void saveOrUpdateKakaoToken(String accessToken, String refreshToken, MobiUser mobiUser) {
        // DB에서 사용자 토큰 조회
        KakaoToken kakaoToken = kakaoTokenRepository.findByMobiUser(mobiUser).orElse(null);
        // 토큰이 이미 존재하면 업데이트, 없으면 생성
        if (kakaoToken != null) {
            kakaoToken.setAccessValue(accessToken);
            kakaoToken.setRefreshValue(refreshToken);
            return;
        }
        kakaoToken = KakaoToken.builder()
                .accessValue(accessToken)
                .refreshValue(refreshToken)
                .build();

        mobiUser.addKakaoToken(kakaoToken);
        kakaoToken.setMobiUser(mobiUser);

        kakaoTokenRepository.save(kakaoToken);
    }
}
