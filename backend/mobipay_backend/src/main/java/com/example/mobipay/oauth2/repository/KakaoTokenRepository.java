package com.example.mobipay.oauth2.repository;

import com.example.mobipay.domain.kakaotoken.entity.KakaoToken;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KakaoTokenRepository extends JpaRepository<KakaoToken, Long> {
    Optional<KakaoToken> findByMobiUser(MobiUser mobiUser);
}