package com.example.mobipay.domain.ssafyuser.repository;

import com.example.mobipay.domain.ssafyuser.entity.SsafyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SsafyUserRepository extends JpaRepository<SsafyUser, Long> {
}
