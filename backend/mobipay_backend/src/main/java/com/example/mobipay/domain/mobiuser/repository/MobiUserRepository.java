package com.example.mobipay.domain.mobiuser.repository;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobiUserRepository extends JpaRepository<MobiUser, Long> {

    Boolean existsByEmail(String email);

    Optional<MobiUser> findByEmail(String email);
}
