package com.example.mobipay.global.advice;

import com.example.mobipay.domain.car.error.CarNotFoundException;
import com.example.mobipay.domain.car.error.DuplicatedCarNumberException;
import com.example.mobipay.domain.car.error.NotOwnerException;
import com.example.mobipay.domain.mobiuser.error.MobiUserNotFoundException;
import com.example.mobipay.global.error.ErrorCode;
import com.example.mobipay.global.error.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(DuplicatedCarNumberException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicatedCarNumberException(DuplicatedCarNumberException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.DUPLICATED_CAR_NUMBER);
    }

    @ExceptionHandler(MobiUserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleMobiUserNotFoundException(MobiUserNotFoundException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.MOBI_USER_NOT_FOUND);
    }

    @ExceptionHandler(CarNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleCarNotFoundException(CarNotFoundException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.CAR_NOT_FOUND);
    }

    @ExceptionHandler(NotOwnerException.class)
    public ResponseEntity<ErrorResponseDto> handleNotOwnerException(NotOwnerException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.NOT_OWNER);
    }

    private ResponseEntity<ErrorResponseDto> getResponse(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponseDto(errorCode));
    }
}
