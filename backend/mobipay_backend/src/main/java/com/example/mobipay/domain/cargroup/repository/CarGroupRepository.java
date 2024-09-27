package com.example.mobipay.domain.cargroup.repository;

import com.example.mobipay.domain.cargroup.entity.CarGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarGroupRepository extends JpaRepository<CarGroup, Long> {
    Boolean existsByMobiUserIdAndCarId(Long mobiUserId, Long carId);
}
