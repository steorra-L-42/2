package com.example.mobipay.domain.ownedcard.entity;

import com.example.mobipay.domain.common.AuditableCreatedEntity;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import com.example.mobipay.domain.setupdomain.account.entity.Account;
import com.example.mobipay.domain.setupdomain.card.entity.CardProduct;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "owned_card")
public class OwnedCard extends AuditableCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "card_no", nullable = false, unique = true, length = 16)
    private String cardNo;

    @Column(name = "cvc", nullable = false, length = 3)
    private String cvc;

    @Column(name = "withdrawal_date", nullable = false, length = 10)
    private String withdrawalDate;

    // 카드 만료일은 외부 API 정책에 따라 생성일 기준 + 5년으로 설정됨.
    @Column(name = "card_expiry_date", nullable = false, length = 8)
    private String cardExpiryDate;

    @OneToMany(mappedBy = "ownedCard")
    private List<RegisteredCard> registeredCards = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mobi_user_id")
    private MobiUser mobiUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_unique_no")
    private CardProduct cardProduct;
}
