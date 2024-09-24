package com.example.merchant.global.advice;

import com.example.merchant.domain.parking.error.DuplicatedParkingException;
import com.example.merchant.domain.parking.error.InvalidMerApiKeyException;
import com.example.merchant.global.error.ErrorCode;
import com.example.merchant.global.error.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(DuplicatedParkingException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicatedParkingException (DuplicatedParkingException e) {
        log.info("DuplicatedParkingException: " + e.getMessage());
        return getResponse(ErrorCode.DUPLICATEDPARKING);
    }

    @ExceptionHandler(InvalidMerApiKeyException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidMerApiKeyException  (InvalidMerApiKeyException e) {
        log.info("InvalidMerApiKeyException: " + e.getMessage());
        return getResponse(ErrorCode.INVALIDMERAPIKEY);
    }

    private ResponseEntity<ErrorResponseDto> getResponse(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponseDto(errorCode));
    }
}
