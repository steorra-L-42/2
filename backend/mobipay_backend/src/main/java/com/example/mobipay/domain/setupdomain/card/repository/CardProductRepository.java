package com.example.mobipay.domain.setupdomain.card.repository;

import com.example.mobipay.domain.setupdomain.card.entity.CardProduct;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardProductRepository extends JpaRepository<CardProduct, String> {

    Optional<CardProduct> findByCardUniqueNo(String cardUniqueNo);
}
