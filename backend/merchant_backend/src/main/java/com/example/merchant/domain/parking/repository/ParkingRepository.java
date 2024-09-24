package com.example.merchant.domain.parking.repository;

import com.example.merchant.domain.parking.entity.Parking;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingRepository extends JpaRepository<Parking, Long> {

    boolean existsByNumberAndPaidIsFalse(String number);
    List<Parking> findAllByNumberAndPaidFalse(String number);
//    Integer countByNumberAndPaidFalse(String number);
}
