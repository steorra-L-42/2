package com.example.mobipay.oauth2.repository;

import com.example.mobipay.domain.kakaotoken.entity.KakaoToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KakaoTokenRepository extends JpaRepository<KakaoToken, Long> {
    KakaoToken findByMobiUserId(Long mobiUserId);
}