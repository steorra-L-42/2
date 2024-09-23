package com.example.merchant.global.advice;

import com.example.merchant.global.error.ErrorCode;
import com.example.merchant.global.error.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

//     example of exception handler
//    @ExceptionHandler(NotOwnerException.class)
//    public ResponseEntity<ErrorResponseDto> handleNotOwnerException(NotOwnerException e) {
//        log.info(e.getMessage());
//        return getResponse(ErrorCode.NOT_OWNER);
//    }

    private ResponseEntity<ErrorResponseDto> getResponse(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponseDto(errorCode));
    }
}
