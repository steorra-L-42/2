package com.example.mobipay.domain.registeredcard.entity;

import com.example.mobipay.domain.merchanttransaction.entity.MerchantTransaction;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
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
@IdClass(RegisteredCardId.class) // 복합키를 이용한 엔티티를 다른 클래스와 연관관계에 이용할 시 이용하는 어노테이션
@Table(name = "registered_card")
public class RegisteredCard {

    private static final Integer ONE_DAY_LIMIT = 10_000_000;
    private static final Integer ONE_TIME_LIMIT = 1_000_000;

    @Id
    @Column(name = "mobi_user_id")
    private Long mobiUserId;

    @Id
    @Column(name = "owned_card_id")
    private Long ownedCardId;

    @Column(name = "one_day_limit", nullable = false)
    private Integer oneDayLimit = ONE_DAY_LIMIT;

    @Column(name = "one_time_limit", nullable = false)
    private Integer oneTimeLimit = ONE_TIME_LIMIT;

    @Column(name = "auto_pay_status", nullable = false)
    private Boolean autoPayStatus = false;

    @OneToMany(mappedBy = "registeredCard")
    private List<MerchantTransaction> merchantTransactions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mobi_user_id", insertable = false, updatable = false)
    private MobiUser mobiUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owned_card_id", insertable = false, updatable = false)
    private OwnedCard ownedCard;

    public static RegisteredCard from() {
        return new RegisteredCard();
    }

    public static RegisteredCard of(Integer oneDayLimit, Integer oneTimeLimit, Boolean autoPayStatus) {
        RegisteredCard registeredCard = new RegisteredCard();
        registeredCard.oneDayLimit = oneDayLimit;
        registeredCard.oneTimeLimit = oneTimeLimit;
        registeredCard.autoPayStatus = autoPayStatus;

        return registeredCard;
    }

    public void addRelations(MobiUser mobiUser, OwnedCard ownedCard) {
        if (this.mobiUser != null) {
            this.mobiUser.getRegisteredCards().remove(this);
        }
        this.mobiUser = mobiUser;
        this.mobiUserId = mobiUser.getId();
        mobiUser.getRegisteredCards().add(this);

        if (this.ownedCard != null) {
            this.ownedCard.getRegisteredCards().remove(this);
        }
        this.ownedCard = ownedCard;
        this.ownedCardId = ownedCard.getId();
        ownedCard.getRegisteredCards().add(this);
    }

    public void setAutoPayStatus(Boolean autoPayStatus) {
        this.autoPayStatus = autoPayStatus;
    }
}
