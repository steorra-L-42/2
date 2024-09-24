package com.example.mobipay.oauth2.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtResponseDto {

    private String token;         // JWT 토큰
    private LocalDateTime issuedAt;   // 발행일
    private LocalDateTime expiredAt;  // 만료일
    private Boolean revoked;      // 거절 여부

    // 생성자
//    public JwtResponseDto(String token, LocalDateTime issuedAt, LocalDateTime expiredAt, Boolean revoked) {
//        this.token = token;
//        this.issuedAt = issuedAt;
//        this.expiredAt = expiredAt;
//        this.revoked = revoked;
//    }

    // Getter & Setter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public Boolean getRevoked() {
        return revoked;
    }

    public void setRevoked(Boolean revoked) {
        this.revoked = revoked;
    }
}
