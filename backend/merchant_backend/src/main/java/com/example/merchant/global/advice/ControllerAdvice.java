package com.example.merchant.global.advice;

import com.example.merchant.domain.cancel.error.InvalidTransactionUniqueNoException;
import com.example.merchant.domain.parking.error.DuplicatedParkingException;
import com.example.merchant.domain.parking.error.InvalidCarNumberException;
import com.example.merchant.domain.parking.error.InvalidMerApiKeyException;
import com.example.merchant.domain.parking.error.MultipleNotPaidException;
import com.example.merchant.domain.parking.error.NotExistParkingException;
import com.example.merchant.domain.payment.error.InvalidMerchantTypeException;
import com.example.merchant.global.error.ErrorCode;
import com.example.merchant.global.error.ErrorResponseDto;
import com.example.merchant.util.credential.error.UnknownMerchantIdException;
import com.example.merchant.util.pos.error.WebSocketException;
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
        return getResponse(ErrorCode.DUPLICATED_PARKING);
    }

    @ExceptionHandler(InvalidMerApiKeyException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidMerApiKeyException  (InvalidMerApiKeyException e) {
        log.info("InvalidMerApiKeyException: " + e.getMessage());
        return getResponse(ErrorCode.INVALID_MER_API_KEY);
    }

    @ExceptionHandler(NotExistParkingException.class)
    public ResponseEntity<ErrorResponseDto> handleNotExistParkingException  (NotExistParkingException e) {
        log.info("NotExistParkingException: " + e.getMessage());
        return getResponse(ErrorCode.NOT_EXIST_PARKING);
    }

    @ExceptionHandler(MultipleNotPaidException.class)
    public ResponseEntity<ErrorResponseDto> handleMultipleNotPaidException  (MultipleNotPaidException e) {
        log.info("MultipleNotPaidException: " + e.getMessage());
        return getResponse(ErrorCode.MULTIPLE_NOT_PAID);
    }

    @ExceptionHandler(WebSocketException.class)
    public ResponseEntity<ErrorResponseDto> handleWebSocketException  (WebSocketException e) {
        log.error("WebSocketException: " + e.getMessage());
        return getResponse(ErrorCode.WEBSOCKET_ERROR);
    }

    @ExceptionHandler(UnknownMerchantIdException.class)
    public ResponseEntity<ErrorResponseDto> handleUnknownMerchantIdException  (UnknownMerchantIdException e) {
        log.error("UnknownMerchantIdException: " + e.getMessage());
        return getResponse(ErrorCode.UNKNOWN_MERCHANTID);
    }

    @ExceptionHandler(InvalidTransactionUniqueNoException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidTransactionUniqueNoException  (InvalidTransactionUniqueNoException e) {
        log.error("InvalidTransactionUniqueNoException: " + e.getMessage());
        return getResponse(ErrorCode.INVALID_TRANSACTION_UNIQUE_NO);
    }

    @ExceptionHandler(InvalidMerchantTypeException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidMerchantTypeException  (InvalidMerchantTypeException e) {
        log.error("InvalidMerchantTypeException: " + e.getMessage());
        return getResponse(ErrorCode.INVALID_MERCHANT_TYPE);
    }

    @ExceptionHandler(InvalidCarNumberException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidCarNumberException  (InvalidCarNumberException e) {
        log.error("InvalidCarNumberException: " + e.getMessage());
        return getResponse(ErrorCode.INVALID_CAR_NUMBER);
    }

    private ResponseEntity<ErrorResponseDto> getResponse(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponseDto(errorCode));
    }
}
