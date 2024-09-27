package com.example.mobipay.domain.cargroup.repository;

import com.example.mobipay.domain.cargroup.entity.CarGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarGroupRepository extends JpaRepository<CarGroup, Long> {
}
