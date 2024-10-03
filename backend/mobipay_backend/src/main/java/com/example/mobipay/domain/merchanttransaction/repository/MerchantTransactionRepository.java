package com.example.mobipay.domain.merchanttransaction.repository;

import com.example.mobipay.domain.merchanttransaction.entity.MerchantTransaction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantTransactionRepository extends JpaRepository<MerchantTransaction, Long> {

    Optional<MerchantTransaction> findByTransactionUniqueNo(Long transactionUniqueNo);

    @Query("select mt from MerchantTransaction mt where mt.registeredCard.mobiUserId = :mobiUserId " +
            "order by mt.transactionDate desc, mt.transactionTime desc")
    List<MerchantTransaction> findByMobiUserId(@Param("mobiUserId") Long mobiUserId);

}
