package com.example.merchant.domain.parking.service;

import com.example.merchant.domain.parking.dto.ParkingEntryRequest;
import com.example.merchant.domain.parking.dto.ParkingEntryResponse;
import com.example.merchant.domain.parking.dto.ParkingEntryTimeResponse;
import com.example.merchant.domain.parking.dto.ParkingExitRequest;
import com.example.merchant.domain.parking.dto.ParkingExitResponse;
import com.example.merchant.domain.parking.entity.Parking;
import com.example.merchant.domain.parking.error.DuplicatedParkingException;
import com.example.merchant.domain.parking.error.InvalidCarNumberException;
import com.example.merchant.domain.parking.error.InvalidMerApiKeyException;
import com.example.merchant.domain.parking.error.MultipleNotPaidException;
import com.example.merchant.domain.parking.error.NotExistParkingException;
import com.example.merchant.domain.parking.repository.ParkingRepository;
import com.example.merchant.util.credential.CredentialUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ParkingService {

    private final ParkingRepository parkingRepository;
    private final CredentialUtil credentialUtil;

    public ParkingEntryTimeResponse entryGet(String carNumber) {

        if (carNumber.length() < 7 || carNumber.length() > 8) {
            throw new InvalidCarNumberException();
        }

        List<Parking> parkings = parkingRepository.findAllByNumberAndPaidFalse(carNumber);

        if(parkings.isEmpty()) {
            throw new NotExistParkingException();
        }
        if (parkings.size() > 1) {
            throw new MultipleNotPaidException();
        }

        return ParkingEntryTimeResponse.from(parkings.get(0));
    }

    @Transactional
    public ParkingEntryResponse entryPost(ParkingEntryRequest request, String merApiKey) {

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
        if (credentialUtil.getPOS_MER_API_KEY().equals(merApiKey)) {
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
