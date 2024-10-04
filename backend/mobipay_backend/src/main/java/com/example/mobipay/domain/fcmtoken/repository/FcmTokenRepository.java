package com.example.mobipay.domain.fcmtoken.repository;

import com.example.mobipay.domain.fcmtoken.entity.FcmToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByValue(String fcmTokenValue);

    void deleteByValue(String fcmTokenValue);
}
