//package com.example.mobipay.oauth2.entity;
//
//import com.example.mobipay.domain.mobiuser.entity.MobiUser;
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.OneToOne;
//import jakarta.persistence.Table;
//import java.time.LocalDateTime;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Table(name = "refresh_token")
//public class RefreshToken {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private Long id;
//
//    @Column(nullable = false, columnDefinition = "TEXT")
//    private String value;
//
//    @Column(nullable = false)
//    private LocalDateTime issuedAt;
//
//    @Column(nullable = false)
//    private LocalDateTime expiredAt;
//
//    @Column(nullable = false)
//    private Boolean revoked = false;
//
//    @OneToOne(mappedBy = "refreshToken", fetch = FetchType.LAZY)
//    private MobiUser mobiUser;
//}
//
