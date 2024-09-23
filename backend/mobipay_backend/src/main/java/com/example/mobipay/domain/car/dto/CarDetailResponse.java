package com.example.mobipay.domain.car.dto;

import com.example.mobipay.domain.car.entity.Car;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CarDetailResponse {
    private Long carId;
    private String number;
    private LocalDateTime created;
    private Boolean autoPayStatus;
    private Long ownerId;

    public static CarDetailResponse from(Car car) {
        return CarDetailResponse.builder()
                .carId(car.getId())
                .number(car.getNumber())
                .created(car.getCreated())
                .autoPayStatus(car.getAutoPayStatus())
                .ownerId(car.getOwner().getId())
                .build();
    }
}
