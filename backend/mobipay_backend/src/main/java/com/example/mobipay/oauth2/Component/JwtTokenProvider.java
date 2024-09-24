package com.example.mobipay.oauth2.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    @Value("${spring.security.oauth2.client.registration.kakao.client-authentication-method}")
    private String nosecretKey; // 비밀키 (외부 설정으로 관리하는 것이 좋음)
    private final long validityInMilliseconds = 365 * 24 * 60 * 60 * 1000L; // 1년ㅋㅋ 유효
    private String secretKey;

    @PostConstruct  // 빈이 생성된 후 비밀키를 인코딩
    public void init() {
        // Base64 URL-safe 인코딩으로 비밀키를 변환
        this.secretKey = Base64.getUrlEncoder().withoutPadding().encodeToString(padSecretKey(nosecretKey).getBytes());
    }

    // 비밀키를 패딩해서 최소 32바이트 길이를 맞춤
    private String padSecretKey(String key) {
        if (key.length() < 32) {
            StringBuilder paddedKey = new StringBuilder(key);
            while (paddedKey.length() < 32) {
                paddedKey.append("0");  // 32바이트가 될 때까지 0을 추가
            }
            return paddedKey.toString();
        }
        return key;
    }

    // JWT 토큰 생성 메서드
    public String createToken(String email, String name, String picture, String phoneNumber) {
        Map<String, Object> claims = new HashMap<>(); // 이메일을 토큰 주제로 설정
        claims.put("name", name);
        claims.put("picture", picture);
        claims.put("phoneNumber", phoneNumber);
//        claims.put("role", role);  // 역할 정보를 추가

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
