package com.example.mobipay.domain.car.dto;

import com.example.mobipay.domain.car.entity.Car;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CarListResponse {

    private final List<CarDetailResponse> items;

    public static CarListResponse from(List<Car> cars) {
        List<CarDetailResponse> items = cars.stream()
                .map(CarDetailResponse::from)
                .toList();

        return new CarListResponse(items);
    }
}
