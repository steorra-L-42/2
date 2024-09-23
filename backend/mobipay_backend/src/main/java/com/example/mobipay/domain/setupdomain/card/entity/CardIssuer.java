package com.example.mobipay.domain.setupdomain.card.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "card_issuer")
public class CardIssuer {

    @Id
    @Column(name = "card_issuer_code", length = 4)
    private String cardIssuerCode;

    @Column(name = "card_issuer_name", nullable = false, length = 20)
    private String cardIssuerName;

    @OneToMany(mappedBy = "cardIssuer")
    private List<CardProduct> cardProducts = new ArrayList<>();
}
