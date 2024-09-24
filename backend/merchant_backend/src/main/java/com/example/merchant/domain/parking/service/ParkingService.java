package com.example.merchant.domain.parking.service;

import com.example.merchant.domain.parking.dto.ParkingEntryRequest;
import com.example.merchant.domain.parking.dto.ParkingEntryResponse;
import com.example.merchant.domain.parking.dto.ParkingExitRequest;
import com.example.merchant.domain.parking.dto.ParkingExitResponse;
import com.example.merchant.domain.parking.entity.Parking;
import com.example.merchant.domain.parking.error.DuplicatedParkingException;
import com.example.merchant.domain.parking.error.InvalidMerApiKeyException;
import com.example.merchant.domain.parking.error.MultipleNotPaidException;
import com.example.merchant.domain.parking.error.NotExistParkingException;
import com.example.merchant.domain.parking.repository.ParkingRepository;
import java.util.List;
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

    @Transactional
    public ParkingExitResponse exit(ParkingExitRequest request, String merApiKey) {

        validateMerApiKey(merApiKey);

        List<Parking> parkings = parkingRepository.findAllByNumberAndPaidFalse(request.getCarNumber());
        validateExistParking(parkings);
        validateMultipleNotPaid(parkings);

       Parking exitedParking = parkings.get(0).goExit(request.getExit());

        return ParkingExitResponse.of(exitedParking);
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

    private void validateExistParking(List<Parking> parkings) {
        if (parkings.isEmpty()) {
            throw new NotExistParkingException();
        }
    }

    private void validateMultipleNotPaid(List<Parking> parkings) {
        if (parkings.size() > 1) {
            throw new MultipleNotPaidException();
        }
    }
}