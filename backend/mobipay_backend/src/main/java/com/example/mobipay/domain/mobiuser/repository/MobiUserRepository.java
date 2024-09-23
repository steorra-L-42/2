package com.example.mobipay.domain.mobiuser.repository;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MobiUserRepository extends JpaRepository<MobiUser, Long> {
}
