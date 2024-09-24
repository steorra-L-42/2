package com.example.merchant.domain.parking.service;

import com.example.merchant.domain.parking.dto.ParkingEntryRequest;
import com.example.merchant.domain.parking.dto.ParkingEntryResponse;
import com.example.merchant.domain.parking.entity.Parking;
import com.example.merchant.domain.parking.error.DuplicatedParkingException;
import com.example.merchant.domain.parking.error.InvalidMerApiKeyException;
import com.example.merchant.domain.parking.repository.ParkingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ParkingService {

    @Value("${merchant.api.key}")
    private String MER_API_KEY;

    private final ParkingRepository parkingRepository;

    @Transactional
    public ParkingEntryResponse entry(ParkingEntryRequest request, String merApiKey) {

        validateMerApiKey(merApiKey);
        validateDuplicateParking(request.getCarNumber());

        Parking parking = parkingRepository.save(Parking.of(request));

        return ParkingEntryResponse.of(parking);
    }

    private void validateMerApiKey(String merApiKey) {
        if (MER_API_KEY.equals(merApiKey)) {
            return;
        }
        throw new InvalidMerApiKeyException();
    }

    private void validateDuplicateParking(String carNumber) {
        boolean duplicated = parkingRepository.existsByNumberAndPaidIsFalse(carNumber);
        if (duplicated) {
            throw new DuplicatedParkingException();
        }
    }
}
