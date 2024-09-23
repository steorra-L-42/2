package com.example.mobipay.domain.cargroup.entity;

import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(CarGroupId.class)
@Table(name = "car_group")
public class CarGroup {

    @Id
    @Column(name = "car_id")
    private Long carId;

    @Id
    @Column(name = "mobi_user_id")
    private Long mobiUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", insertable = false, updatable = false)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mobi_user_id", insertable = false, updatable = false)
    private MobiUser mobiUser;

    public static CarGroup of(Car car, MobiUser mobiUser) {
        CarGroup carGroup = new CarGroup();
        carGroup.carId = car.getId(); // carId 설정
        carGroup.mobiUserId = mobiUser.getId(); // mobiUserId 설정
        carGroup.addRelation(car, mobiUser);
        return carGroup;
    }

    // 연관관계 설정 메서드
    public void addRelation(Car car, MobiUser mobiUser) {
        if (this.car != null) {
            this.car.getCarGroups().remove(this);
        }
        this.car = car;
        car.getCarGroups().add(this);

        if (this.mobiUser != null) {
            this.mobiUser.getCarGroups().remove(this);
        }
        this.mobiUser = mobiUser;
        mobiUser.getCarGroups().add(this);
    }
}
