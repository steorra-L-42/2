package com.example.mobipay.domain.registeredcard.repository;

import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisteredCardRepository extends JpaRepository<RegisteredCard, Long> {
}
