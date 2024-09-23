package com.example.mobipay.oauth2.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    @Value("${spring.security.oauth2.client.registration.kakao.client-authentication-method}")
    private String secretKey; // 비밀키 (외부 설정으로 관리하는 것이 좋음)
    private final long validityInMilliseconds = 3600000; // 1시간 유효

    // JWT 토큰 생성 메서드
    public String createToken(String email, String name, String picture, String phoneNumber, String role) {
        Claims claims = (Claims) Jwts.claims().setSubject(email); // 이메일을 토큰 주제로 설정
        claims.put("name", name);
        claims.put("picture", picture);
        claims.put("phoneNumber", phoneNumber);
        claims.put("role", role);  // 역할 정보를 추가

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds); // 토큰 유효 시간 설정

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)  // 만료 시간 설정
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 서명 알고리즘과 비밀키로 서명
                .compact();
    }
}
