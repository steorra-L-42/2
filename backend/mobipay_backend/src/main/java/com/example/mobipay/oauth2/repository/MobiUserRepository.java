package com.example.mobipay.oauth2.repository;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobiUserRepository extends JpaRepository<MobiUser, Long> {

    MobiUser findByEmail(String email);
}
