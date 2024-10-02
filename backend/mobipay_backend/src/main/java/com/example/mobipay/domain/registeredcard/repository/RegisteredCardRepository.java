package com.example.mobipay.domain.registeredcard.repository;

import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import com.example.mobipay.domain.registeredcard.entity.RegisteredCardId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisteredCardRepository extends JpaRepository<RegisteredCard, RegisteredCardId> {

    Optional<RegisteredCard> findByMobiUserIdAndAutoPayStatus(Long mobiUserId, Boolean autoPayStatus);

    List<RegisteredCard> findAllByMobiUserIdAndAutoPayStatus(Long mobiUserId, Boolean autoPayStatus);

    Optional<RegisteredCard> findByOwnedCardIdAndMobiUserId(Long ownedCardId, Long mobiUserId);

    List<RegisteredCard> findByMobiUserId(Long mobiUserId);
}
