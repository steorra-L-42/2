package com.example.mobipay.domain.car.dto;

import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.cargroup.entity.CarGroup;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CarMemberListResponse {

    private final List<CarMemberDetailResponse> items;

    public static CarMemberListResponse from(Car car) {

        List<CarMemberDetailResponse> items = car.getCarGroups().stream()
                .map(CarGroup::getMobiUser)
                .map(CarMemberDetailResponse::from)
                .toList();

        return new CarMemberListResponse(items);
    }
}
