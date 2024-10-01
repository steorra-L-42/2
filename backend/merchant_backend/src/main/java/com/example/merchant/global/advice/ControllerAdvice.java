package com.example.merchant.global.advice;

import com.example.merchant.domain.parking.error.DuplicatedParkingException;
import com.example.merchant.domain.parking.error.InvalidMerApiKeyException;
import com.example.merchant.domain.parking.error.MultipleNotPaidException;
import com.example.merchant.domain.parking.error.NotExistParkingException;
import com.example.merchant.global.error.ErrorCode;
import com.example.merchant.global.error.ErrorResponseDto;
import com.example.merchant.util.pos.error.WebSocketException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.server.WebServerException;
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

    @ExceptionHandler(NotExistParkingException.class)
    public ResponseEntity<ErrorResponseDto> handleNotExistParkingException  (NotExistParkingException e) {
        log.info("NotExistParkingException: " + e.getMessage());
        return getResponse(ErrorCode.NOTEXISTPARKING);
    }

    @ExceptionHandler(MultipleNotPaidException.class)
    public ResponseEntity<ErrorResponseDto> handleMultipleNotPaidException  (MultipleNotPaidException e) {
        log.info("MultipleNotPaidException: " + e.getMessage());
        return getResponse(ErrorCode.MULTIPLENOTPAID);
    }

    @ExceptionHandler(WebSocketException.class)
    public ResponseEntity<ErrorResponseDto> handleWebSocketException  (WebSocketException e) {
        log.error("WebSocketException: " + e.getMessage());
        return getResponse(ErrorCode.WEBSOCKETERROR);
    }

    @ExceptionHandler(WebServerException.class)
    public ResponseEntity<ErrorResponseDto> handleWebServerException  (WebServerException e) {
        log.error("WebServerException: " + e.getMessage());
        return getResponse(ErrorCode.WEBSOCKETERROR);
    }

    private ResponseEntity<ErrorResponseDto> getResponse(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponseDto(errorCode));
    }
}
