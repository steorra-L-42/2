package com.example.merchant.domain.parking.dto;

import com.example.merchant.domain.parking.entity.Parking;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParkingExitResponse {
    private Long parkingId;
    private String carNumber;
    private LocalDateTime entry;
    private LocalDateTime exit;
    private Boolean paid;

    public static ParkingExitResponse of(Parking parking) {
        return ParkingExitResponse.builder()
            .parkingId(parking.getId())
            .carNumber(parking.getNumber())
            .entry(parking.getEntry())
            .exit(parking.getExit())
            .paid(parking.getPaid())
            .build();
    }
}