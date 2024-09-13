package com.example.mobipay.domain.cargroup.entity;

import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "car_group")
public class CarGroup {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mobi_user_id")
    private MobiUser mobiUser;
}
