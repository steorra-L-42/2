package com.example.mobipay.domain.car.repository;

import com.example.mobipay.domain.car.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Boolean existsByNumber(String number);
}
