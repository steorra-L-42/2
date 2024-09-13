package com.example.mobipay.domain.ssafyuser.entity;

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
@Table(name = "ssafy_user")
public class SsafyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true, length = 40)
    private String userId;

    @Column(name = "username", nullable = false, unique = true, length = 10)
    private String username;

    @Column(name = "user_key", nullable = false, unique = true, length = 60)
    private String userKey;

    @Column(name = "created", nullable = false, length = 10)
    private String created;

    @Column(name = "modified", nullable = false, length = 10)
    private String modified;

    @OneToOne(mappedBy = "ssafyUser", fetch = FetchType.LAZY)
    private MobiUser mobiUser;
}
