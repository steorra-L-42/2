package com.example.mobipay.domain.setupdomain.account.repository;

import com.example.mobipay.domain.setupdomain.account.entity.AccountProduct;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountProductRepository extends JpaRepository<AccountProduct, Long> {

    Optional<AccountProduct> findByAccountTypeUniqueNo(String accountTypeUniqueNo);

    Optional<AccountProduct> findByAccountName(String accountName);
}
