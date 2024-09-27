package com.example.mobipay.oauth2.jwt;

import static com.example.mobipay.oauth2.enums.TokenType.ACCESS;
import static com.example.mobipay.oauth2.enums.TokenType.REFRESH;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
//        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());


    }

    // Category 값 얻기
    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get("category", String.class);
    }

    public String getEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .get("email", String.class);
    }

//    public Long getUserId(String token) {
//        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
//                .get("userId", Long.class);
//    }

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

//    public String getRole(String token) {
//
//        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
//                .get("role", String.class);
//    }

    // 만료 유무
    public LocalDateTime getIssuedAt(String token) {
        return LocalDateTime.ofInstant(Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .getIssuedAt().toInstant(), ZoneId.systemDefault());
    }

    public LocalDateTime getExpiredAt(String token) {
        return LocalDateTime.ofInstant(Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
                .getExpiration().toInstant(), ZoneId.systemDefault());
    }

    // 만료 유무
    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration()
                .before(new Date());
    }

    public String createAccessToken(String email, String name, String phoneNumber, String picture) {
        System.out.println("Creating refresh token for user: " + email);
        return createJwt(ACCESS.getType(), email, name, phoneNumber, picture, ACCESS.getExpiration() * MS_TO_S);
    }

    public String createRefreshToken(String email, String name, String phoneNumber, String picture) {
        System.out.println("Creating refresh token for user: " + email);
        return createJwt(REFRESH.getType(), email, name, phoneNumber, picture, REFRESH.getExpiration() * MS_TO_S);
    }

    public String createJwt(String category, String email, String name, String phoneNumber, String picture,
                            Long expiredMs) {
        System.out.println("Creating JWT with category: " + category);
        System.out.println("User info: " + email + ", " + name + ", " + phoneNumber + ", " + picture);

        String jwt = Jwts.builder()
                .claim("category", category) // access, refresh 판단
                .claim("email", email)
                .claim("name", name)
                .claim("phoneNumber", phoneNumber)
                .claim("picture", picture)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        System.out.println("jwt" + jwt);
        return jwt;
    }
}
