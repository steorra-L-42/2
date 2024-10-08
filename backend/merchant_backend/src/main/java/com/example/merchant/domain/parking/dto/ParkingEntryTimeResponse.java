package com.example.merchant.domain.parking.dto;

import com.example.merchant.domain.parking.entity.Parking;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ParkingEntryTimeResponse {

    private Long parkingId;
    private String carNumber;
    private LocalDateTime entry;

    public static ParkingEntryTimeResponse from(Parking parking) {
        return ParkingEntryTimeResponse.builder()
                .parkingId(parking.getId())
                .carNumber(parking.getNumber())
                .entry(parking.getEntry())
                .build();
    }
}
