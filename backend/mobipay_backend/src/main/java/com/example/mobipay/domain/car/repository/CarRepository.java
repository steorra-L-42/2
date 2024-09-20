package com.example.mobipay.domain.car.repository;

import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Boolean existsByNumber(String number);

    List<Car> findAllByOwner(MobiUser mobiUser);

    Optional<Car> findByNumber(String number);
}
