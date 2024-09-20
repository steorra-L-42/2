package com.example.mobipay.oauth2.jwt;

import static com.example.mobipay.oauth2.enums.TokenType.ACCESS;
import static com.example.mobipay.oauth2.enums.TokenType.REFRESH;

import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {

    private static final Long MS_TO_S = 1000L;
    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {

        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getEmail(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get("email", String.class);
    }

    public Long getUserId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get("userId", Long.class);
    }

    public String getName(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get("name", String.class);
    }

    public String getPhoneNumber(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get("phoneNumber", String.class);
    }

    public String getPicture(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get("picture", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get("role", String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration()
                .before(new Date());
    }

    public String createAccessToken(String email, Long userId, String name, String phoneNumber, String picture,
                                    String role) {
        return createJwt(ACCESS.getType(), email, userId, name, phoneNumber, picture, role,
                ACCESS.getExpiration() * MS_TO_S);
    }

    public String createRefreshToken(String email, Long userId, String name, String phoneNumber, String picture,
                                     String role) {
        return createJwt(REFRESH.getType(), email, userId, name, phoneNumber, picture, role,
                REFRESH.getExpiration() * MS_TO_S);
    }

    public String createJwt(String category, String email, Long userId, String name, String phoneNumber, String picture,
                            String role,
                            Long expiredMs) {

        return Jwts.builder()
                .claim("category", category) // access, refresh 판단
                .claim("email", email)
                .claim("userId", userId)
                .claim("name", name)
                .claim("phoneNumber", phoneNumber)
                .claim("picture", picture)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
}
