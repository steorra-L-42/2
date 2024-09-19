package com.example.mobipay.domain.mobiuser.entity;

import static com.example.mobipay.domain.mobiuser.enums.Role.USER;

import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.cargroup.entity.CarGroup;
import com.example.mobipay.domain.common.AuditableCreatedEntity;
import com.example.mobipay.domain.invitation.entity.Invitation;
import com.example.mobipay.domain.kakaotoken.entity.KakaoToken;
import com.example.mobipay.domain.mobiuser.enums.Role;
import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import com.example.mobipay.domain.refreshtoken.entity.RefreshToken;
import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import com.example.mobipay.domain.setupdomain.account.entity.Account;
import com.example.mobipay.domain.ssafyuser.entity.SsafyUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mobi_user")
public class MobiUser extends AuditableCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 40)
    private String email;

    @Column(name = "password", nullable = false, length = 25)
    private String password;

    @Column(name = "name", nullable = false, length = 25)
    private String name;

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "picture", nullable = false)
    private String picture;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = USER;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refresh_token_id")
    private RefreshToken refreshToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kakao_token_id")
    private KakaoToken kakaoToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ssafy_user_id")
    private SsafyUser ssafyUser;

    @OneToMany(mappedBy = "owner")
    private List<Car> cars;

    @OneToMany(mappedBy = "mobiUser")
    private List<CarGroup> carGroups;

    @OneToMany(mappedBy = "mobiUser")
    private List<Invitation> invitations;

    @OneToMany(mappedBy = "mobiUser")
    private List<RegisteredCard> registeredCards;

    @OneToMany(mappedBy = "mobiUser")
    private List<OwnedCard> ownedCards;

    @OneToMany(mappedBy = "mobiUser")
    private List<Account> accounts;

    public static MobiUser create(String email, String name, String phoneNumber, String picture) {
        MobiUser mobiUser = new MobiUser();
        mobiUser.email = email;
        mobiUser.name = name;
        mobiUser.phoneNumber = phoneNumber;
        mobiUser.picture = picture;
        mobiUser.role = USER; // 기본 역할 설정
        return mobiUser;
    }
}
