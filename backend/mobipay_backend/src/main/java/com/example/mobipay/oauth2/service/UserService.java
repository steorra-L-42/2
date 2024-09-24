package com.example.mobipay.oauth2.service;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.refreshtoken.entity.RefreshToken;
import com.example.mobipay.oauth2.Component.JwtTokenProvider;
import com.example.mobipay.oauth2.jwt.JWTUtil;
import com.example.mobipay.oauth2.repository.MobiUserRepository;
import com.example.mobipay.oauth2.repository.RefreshTokenRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final MobiUserRepository mobiUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JWTUtil jwtUtil;

    // 1. 이메일로 사용자 조회(중복 유무 확인 후, 중복이 되지 않는다면 createUser 수행)
    public MobiUser findOrCreateUser(String email, String name, String phoneNumber, String picture) {
        // 전화번호가 null 이거나 empty이면 죽여버림
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("PhoneNumber cannot be null or empty:findOrCreateUser");
        }
        return mobiUserRepository.findByEmail(email)
                .orElseGet(() -> createUser(email, name, phoneNumber, picture));
        // 싼피 계정 조회 로직 추가 필요

    }


    // 2. 사용자 생성 (카카오 API에서 받아온 정보 기반)
    public MobiUser createUser(String email, String name, String phoneNumber, String picture) {
        // 전화번호가 null 이거나 empty이면 죽여버림
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("PhoneNumber cannot be null or empty:createUser");
        }

        MobiUser user = MobiUser.builder()
                .email(email)
                .name(name)
                .phoneNumber(phoneNumber)
                .picture(picture)
                .build();
        return mobiUserRepository.save(user);
    }

    // 3. 사용자 정보 업데이트
    public MobiUser updateUser(Long userId, String name, String phoneNumber, String picture) {
        MobiUser mobiUser = mobiUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        mobiUser.updatePicture(picture);
        // 추가로 이름, 전화번호 등 업데이트할 수 있는 로직 작성
        return mobiUser;
    }

    // 4. 리프레시 토큰 추가
    public void addRefreshToken(Long userId, String refreshTokenValue, LocalDateTime issuedAt,
                                LocalDateTime expiresAt) {
        MobiUser mobiUser = mobiUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        RefreshToken refreshToken = RefreshToken.builder()
                .value(refreshTokenValue)
                .issuedAt(issuedAt)
                .expiredAt(expiresAt)
                .build();

        // 리프레시 토큰 저장
        refreshTokenRepository.save(refreshToken);
        // MobiUser와 연관 설정
        mobiUser.addRefreshToken(refreshToken);
        // MobiUser 업데이트
        mobiUserRepository.save(mobiUser);
    }

    //    public String generateJwtToken(MobiUser mobiUser) {}
    // 사용자 정보를 기반으로 JWT 생성
//        return jwtTokenProvider.createToken(
//                mobiUser.getEmail(),
//                mobiUser.getName(),
//                mobiUser.getPicture(),
//                mobiUser.getPhoneNumber()
////                mobiUser.getRole().name()
//        );
    public String generateJwtAccessToken(MobiUser mobiUser) {
        return jwtUtil.createAccessToken(
                mobiUser.getEmail(),
                mobiUser.getName(),
                mobiUser.getPicture(),
                mobiUser.getPhoneNumber()
        );
    }

    public String generateJwtRefreshToken(MobiUser mobiUser) {
        return jwtUtil.createRefreshToken(
                mobiUser.getEmail(),
                mobiUser.getName(),
                mobiUser.getPicture(),
                mobiUser.getPhoneNumber()
        );
    }

    // 5. 리프레시 토큰 삭제
    public void deleteRefreshToken(Long userId) {
        MobiUser mobiUser = mobiUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        mobiUser.deleteRefreshToken();
    }

}
