package com.example.mobipay.domain.fcmtoken.entity;

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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Table(name = "fcm_token")
public class FcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String value;

    @OneToOne(mappedBy = "fcmToken", fetch = FetchType.LAZY)
    private MobiUser mobiUser;

    private FcmToken(String value) {
        this.value = value;
    }

    public static FcmToken from(String value) {
        return new FcmToken(value);
    }

    public void changeMobiUser(MobiUser mobiUser) {
        this.mobiUser = mobiUser;
    }
}
