package com.example.mobipay.domain.ownedcard.repository;

import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnedCardRepository extends JpaRepository<OwnedCard, Long> {
    List<OwnedCard> findByMobiUserId(Long mobiUserId);
}
