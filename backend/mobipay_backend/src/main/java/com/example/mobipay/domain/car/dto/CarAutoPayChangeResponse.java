package com.example.mobipay.domain.car.dto;

import com.example.mobipay.domain.car.entity.Car;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CarAutoPayChangeResponse {

    private Long carId;
    private String number;
    private LocalDateTime created;
    private Boolean autoPayStatus;
    private Long ownerId;

    public static CarAutoPayChangeResponse of(Car car) {
        return CarAutoPayChangeResponse.builder()
                .carId(car.getId())
                .number(car.getNumber())
                .created(car.getCreated())
                .autoPayStatus(car.getAutoPayStatus())
                .ownerId(car.getOwner().getId())
                .build();
    }

}
