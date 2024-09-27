package com.example.mobipay.domain.mobiuser.repository;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MobiUserRepository extends JpaRepository<MobiUser, Long> {

    Optional<MobiUser> findByEmail(String email);

    boolean existsByEmail(String email);
}
