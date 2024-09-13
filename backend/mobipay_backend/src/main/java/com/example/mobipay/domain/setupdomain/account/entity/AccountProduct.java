package com.example.mobipay.domain.setupdomain.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "account_product")
public class AccountProduct {

    @Id
    @Column(name = "account_type_unique_no", length = 20)
    private String accountTypeUniqueNo;

    @Column(name = "account_name", nullable = false, length = 20)
    private String accountName;

    @OneToMany(mappedBy = "accountProduct")
    private List<Account> accounts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_code")
    private Bank bank;
}
