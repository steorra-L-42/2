package com.example.mobipay.domain.cargroup.repository;

import com.example.mobipay.domain.cargroup.entity.CarGroup;
import com.example.mobipay.domain.cargroup.entity.CarGroupId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarGroupRepository extends JpaRepository<CarGroup, CarGroupId> {
    Boolean existsByMobiUserIdAndCarId(Long mobiUserId, Long carId);

    List<CarGroup> findByCarId(Long carId);

    List<CarGroup> findByMobiUserId(Long mobiUserId);
}
