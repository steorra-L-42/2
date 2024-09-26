package com.example.mobipay.global.advice;

import com.example.mobipay.global.error.ErrorCode;
import com.example.mobipay.global.error.ErrorResponseDto;
import com.example.mobipay.oauth2.error.MissingUserDetailsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(MissingUserDetailsException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingUserDetailsException(MissingUserDetailsException e) {
        String email = e.getEmail();
        String picture = e.getPicture();

//        log.info(e.getMessage());
        String message = String.format("Email: %s, Picture: %s, Message: %s", email, picture, e.getMessage());

        return getResponse(ErrorCode.NOT_NAME_AND_PHONENUMBER);
    }

    private ResponseEntity<ErrorResponseDto> getResponse(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponseDto(errorCode));
    }
}
