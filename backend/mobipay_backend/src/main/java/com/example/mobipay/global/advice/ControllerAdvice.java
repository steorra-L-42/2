package com.example.mobipay.global.advice;

import com.example.mobipay.domain.car.error.CarNotFoundException;
import com.example.mobipay.domain.car.error.DuplicatedCarNumberException;
import com.example.mobipay.domain.car.error.NotMemberException;
import com.example.mobipay.domain.car.error.NotOwnerException;
import com.example.mobipay.domain.invitation.error.AlreadyDecidedException;
import com.example.mobipay.domain.invitation.error.AlreadyInvitedException;
import com.example.mobipay.domain.invitation.error.InvitationNoFoundException;
import com.example.mobipay.domain.invitation.error.NotApprovedOrRejectedException;
import com.example.mobipay.domain.invitation.error.NotInvitedException;
import com.example.mobipay.domain.merchant.error.MerchantNotFoundException;
import com.example.mobipay.domain.mobiuser.error.MobiUserNotFoundException;
import com.example.mobipay.domain.postpayments.error.InvalidMobiApiKeyException;
import com.example.mobipay.global.authentication.error.AccountProductNotFoundException;
import com.example.mobipay.global.authentication.error.CardProductNotFoundException;
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

    @ExceptionHandler(NotMemberException.class)
    public ResponseEntity<ErrorResponseDto> handleNotMemberException(NotMemberException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.NOT_MEMBER);
    }

    @ExceptionHandler(AccountProductNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleAccountProductNotFoundException(AccountProductNotFoundException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.ACCOUNT_PRODUCT_NOT_FOUND);
    }

    @ExceptionHandler(CardProductNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleCardProductNotFoundException(CardProductNotFoundException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.CARD_PRODUCT_NOT_FOUND);
    }

    @ExceptionHandler(AlreadyInvitedException.class)
    public ResponseEntity<ErrorResponseDto> handleAlreadyInvitedException(AlreadyInvitedException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.ALREADY_INVITED);
    }

    @ExceptionHandler(InvitationNoFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleInvitationNotFoundException(InvitationNoFoundException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.INVITATION_NOT_FOUND);
    }

    @ExceptionHandler(AlreadyDecidedException.class)
    public ResponseEntity<ErrorResponseDto> handleAlreadyDecidedException(AlreadyDecidedException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.INVITATION_ALREADY_DECIDED);
    }

    @ExceptionHandler(NotApprovedOrRejectedException.class)
    public ResponseEntity<ErrorResponseDto> handleWaitingNotAllowedException(NotApprovedOrRejectedException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.NOT_APPROVED_OR_REJECTED);
    }

    @ExceptionHandler(NotInvitedException.class)
    public ResponseEntity<ErrorResponseDto> handleNotInvitedException(NotInvitedException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.NOT_INVITED);
    }

    @ExceptionHandler(MerchantNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleMerchantNotFoundException(MerchantNotFoundException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.MERCHANT_NOT_FOUND);
    }

    @ExceptionHandler(InvalidMobiApiKeyException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidMobiApiKeyException(InvalidMobiApiKeyException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.INVALID_MOBI_API_KEY);
    }

    private ResponseEntity<ErrorResponseDto> getResponse(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponseDto(errorCode));
    }
}


