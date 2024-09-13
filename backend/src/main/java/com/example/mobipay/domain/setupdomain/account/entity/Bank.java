package com.example.mobipay.domain.setupdomain.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bank")
public class Bank {

    @Id
    @Column(name = "bank_code", length = 3)
    private String bankCode;

    @Column(name = "bank_name", nullable = false, length = 20)
    private String bankName;

    @OneToMany(mappedBy = "bank")
    private List<AccountProduct> accountProducts;
}
