package com.example.mobipay.domain.setupdomain.account.entity;

import com.example.mobipay.domain.common.entity.AuditableCreatedEntity;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
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
@Table(name = "account")
public class Account extends AuditableCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "account_no", nullable = false, unique = true, length = 16)
    private String accountNo;

    @Column(name = "bank_code", nullable = false, length = 3)
    private String bankCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mobi_user_id")
    private MobiUser mobiUser;

    @OneToMany(mappedBy = "account")
    private List<OwnedCard> ownedCards = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_type_unique_no")
    private AccountProduct accountProduct;
}
