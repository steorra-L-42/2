package com.example.merchant.domain.parking.controller;

import com.example.merchant.domain.parking.dto.PaidChangeRequest;
import com.example.merchant.domain.parking.dto.PaidChangeResponse;
import com.example.merchant.domain.parking.dto.ParkingEntryRequest;
import com.example.merchant.domain.parking.dto.ParkingEntryResponse;
import com.example.merchant.domain.parking.dto.ParkingEntryTimeResponse;
import com.example.merchant.domain.parking.dto.ParkingExitRequest;
import com.example.merchant.domain.parking.dto.ParkingExitResponse;
import com.example.merchant.domain.parking.service.ParkingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/merchants/parking")
@RestController
public class ParkingController {

    private final ParkingService parkingService;

    @GetMapping("/cars/{carNumber}/entry")
    public ResponseEntity<ParkingEntryTimeResponse> entryGet(@PathVariable("carNumber") String carNumber) {

        ParkingEntryTimeResponse response = parkingService.entryGet(carNumber);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/entry")
    public ResponseEntity<ParkingEntryResponse> entryPost(@RequestBody @Valid ParkingEntryRequest request,
                                                          @RequestHeader("merApiKey") String merApiKey) {

        ParkingEntryResponse response = parkingService.entryPost(request, merApiKey);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/exit")
    public ResponseEntity<ParkingExitResponse> exit(@RequestBody @Valid ParkingExitRequest request,
                                                    @RequestHeader("merApiKey") String merApiKey) {

        ParkingExitResponse response = parkingService.exit(request, merApiKey);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/paid")
    public ResponseEntity<PaidChangeResponse> changePaid(@RequestBody @Valid PaidChangeRequest request,
                                                         @RequestHeader("merApiKey") String merApiKey) {

        PaidChangeResponse response = parkingService.changePaid(request, merApiKey);

        return ResponseEntity.ok(response);
    }
}
