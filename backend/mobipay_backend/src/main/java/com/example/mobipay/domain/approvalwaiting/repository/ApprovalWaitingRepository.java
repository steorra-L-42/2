package com.example.mobipay.domain.approvalwaiting.repository;

import com.example.mobipay.domain.approvalwaiting.entity.ApprovalWaiting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalWaitingRepository extends JpaRepository<ApprovalWaiting, Long> {
}
