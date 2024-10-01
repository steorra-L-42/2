package com.example.mobipay.domain.merchanttransaction.repository;

import com.example.mobipay.domain.merchanttransaction.entity.MerchantTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantTransactionRepository extends JpaRepository<MerchantTransaction, Long> {
}
