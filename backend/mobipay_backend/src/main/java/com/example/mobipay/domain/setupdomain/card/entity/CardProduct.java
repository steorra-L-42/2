package com.example.mobipay.domain.setupdomain.card.entity;

import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "card_product")
public class CardProduct {

    @Id
    @Column(name = "card_unique_no", length = 20)
    private String cardUniqueNo;

    @Column(name = "card_name", nullable = false, length = 20)
    private String careName;

    @Column(name = "baseline_performance", nullable = false)
    private Long baselinePerformance;

    @Column(name = "max_benefit_limit", nullable = false)
    private Long maxBenefitLimit;

    @Column(name = "card_description")
    private String cardDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_issuer_no")
    private CardIssuer cardIssuer;

    @OneToMany(mappedBy = "cardProduct")
    private List<OwnedCard> ownedCards = new ArrayList<>();
}
