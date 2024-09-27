package com.example.mobipay.domain.ownedcard.repository;

import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnedCardRepository extends JpaRepository<OwnedCard, String> {
}
