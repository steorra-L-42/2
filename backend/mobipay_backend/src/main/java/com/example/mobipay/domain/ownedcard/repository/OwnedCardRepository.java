package com.example.mobipay.domain.ownedcard.repository;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnedCardRepository extends JpaRepository<OwnedCard, String> {

    Optional<OwnedCard> findByCardNo(String cardNo);

    List<OwnedCard> findAllByMobiUser(MobiUser mobiUser);

    Optional<OwnedCard> findOwnedCardById(Long cardId);


}