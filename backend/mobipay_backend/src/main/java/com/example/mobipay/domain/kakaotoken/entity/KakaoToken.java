package com.example.mobipay.domain.kakaotoken.entity;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "kakao_token")
public class KakaoToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "refresh_value", nullable = false, columnDefinition = "TEXT")
    private String refreshValue;

    @Column(name = "access_value", nullable = false, columnDefinition = "TEXT")
    private String accessValue;

    @OneToOne(mappedBy = "kakaoToken", fetch = FetchType.LAZY)
    private MobiUser mobiUser;
}
