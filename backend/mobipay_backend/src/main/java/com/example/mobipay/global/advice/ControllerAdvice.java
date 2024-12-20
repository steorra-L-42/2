package com.example.mobipay.global.advice;

import com.example.mobipay.domain.approvalwaiting.error.ApprovalWaitingNotFoundException;
import com.example.mobipay.domain.cancel.error.CancelServerException;
import com.example.mobipay.domain.cancel.error.InvalidMerchantTransactionException;
import com.example.mobipay.domain.cancel.error.TransactionAlreadyCancelledException;
import com.example.mobipay.domain.cancel.error.TransactionNotBelongToMerchantException;
import com.example.mobipay.domain.car.error.CarNotFoundException;
import com.example.mobipay.domain.car.error.DuplicatedCarNumberException;
import com.example.mobipay.domain.car.error.NotMemberException;
import com.example.mobipay.domain.car.error.NotOwnerException;
import com.example.mobipay.domain.fcmtoken.error.FCMException;
import com.example.mobipay.domain.invitation.error.AlreadyDecidedException;
import com.example.mobipay.domain.invitation.error.AlreadyInvitedException;
import com.example.mobipay.domain.invitation.error.InvitationNotFoundException;
import com.example.mobipay.domain.invitation.error.NotApprovedOrRejectedException;
import com.example.mobipay.domain.invitation.error.NotInvitedException;
import com.example.mobipay.domain.merchant.error.MerchantNotFoundException;
import com.example.mobipay.domain.merchanttransaction.error.MerchantTransactionNotFoundException;
import com.example.mobipay.domain.mobiuser.error.MobiUserNotFoundException;
import com.example.mobipay.domain.ownedcard.error.OwnedCardNotFoundException;
import com.example.mobipay.domain.postpayments.error.CardTransactionServerError;
import com.example.mobipay.domain.postpayments.error.InvalidCardNoException;
import com.example.mobipay.domain.postpayments.error.InvalidMobiApiKeyException;
import com.example.mobipay.domain.postpayments.error.NotEqualPaymentBalanceException;
import com.example.mobipay.domain.postpayments.error.OneTimeLimitExceedException;
import com.example.mobipay.domain.postpayments.error.ReceiptUserMismatchException;
import com.example.mobipay.domain.postpayments.error.RegisteredCardNotFoundException;
import com.example.mobipay.domain.postpayments.error.TransactionAlreadyApprovedException;
import com.example.mobipay.domain.registeredcard.error.AlreadyRegisteredCard;
import com.example.mobipay.domain.registeredcard.error.CardUserMismatch;
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

    @ExceptionHandler(InvitationNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleInvitationNotFoundException(InvitationNotFoundException e) {
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

    @ExceptionHandler(TransactionAlreadyApprovedException.class)
    public ResponseEntity<ErrorResponseDto> handleTransactionAlreadyApprovedException(
            TransactionAlreadyApprovedException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.TRANSACTION_ALREADY_APPROVED);
    }

    @ExceptionHandler(ApprovalWaitingNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleApprovalWaitingNotFoundException(ApprovalWaitingNotFoundException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.APPROVAL_WAITING_NOT_FOUND);
    }

    @ExceptionHandler(InvalidCardNoException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidCardNoException(InvalidCardNoException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.INVALID_CARD_NO);
    }

    @ExceptionHandler(RegisteredCardNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleRegisteredCardNotFoundException(RegisteredCardNotFoundException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.NOT_REGISTERED_CARD);
    }

    @ExceptionHandler(CardTransactionServerError.class)
    public ResponseEntity<ErrorResponseDto> handleCardTransactionServerError(CardTransactionServerError e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotEqualPaymentBalanceException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidPaymentBalanceException(NotEqualPaymentBalanceException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.NOT_EQUAL_PAYMENT_BALANCE);
    }

    @ExceptionHandler(OwnedCardNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handelOwnedCardNotFoundException(OwnedCardNotFoundException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.NOT_FOUND_CARD);
    }

    @ExceptionHandler(MerchantTransactionNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleMerchantTransactionNotFoundException(
            MerchantTransactionNotFoundException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.MERCHANT_TRANSACTION_NOT_FOUND);
    }

    @ExceptionHandler(ReceiptUserMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleReceiptUserMismatchException(ReceiptUserMismatchException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.RECEIPT_USER_MISMATCH);
    }

    @ExceptionHandler(FCMException.class)
    public ResponseEntity<ErrorResponseDto> handleFCMException(FCMException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.FCM_ERROR);
    }

    @ExceptionHandler(AlreadyRegisteredCard.class)
    public ResponseEntity<ErrorResponseDto> handleAlreadyRegisteredCard(AlreadyRegisteredCard e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.ALREADY_REGISTERED_CARD);
    }

    @ExceptionHandler(CardUserMismatch.class)
    public ResponseEntity<ErrorResponseDto> handleCard(CardUserMismatch e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.CARD_USER_MISMATCH);
    }

    @ExceptionHandler(OneTimeLimitExceedException.class)
    public ResponseEntity<ErrorResponseDto> handleOneTimeLimitExceedException(OneTimeLimitExceedException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.ONE_TIME_LIMIT_EXCEED);
    }

    @ExceptionHandler(InvalidMerchantTransactionException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidMerchantTransactionException(InvalidMerchantTransactionException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.INVALID_MERCHANT_TRANSACTION);
    }

    @ExceptionHandler(TransactionNotBelongToMerchantException.class)
    public ResponseEntity<ErrorResponseDto> handleTransactionNotBelongToMerchantException(TransactionNotBelongToMerchantException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.TRANSACTION_NOT_BELONG_TO_MERCHANT);
    }

    @ExceptionHandler(TransactionAlreadyCancelledException.class)
    public ResponseEntity<ErrorResponseDto> handleTransactionAlreadyCancelledException(
            TransactionAlreadyCancelledException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.TRANSACTION_ALREADY_CANCELLED);
    }

    @ExceptionHandler(CancelServerException.class)
    public ResponseEntity<ErrorResponseDto> handleCancelServerException(CancelServerException e) {
        log.info(e.getMessage());
        return getResponse(ErrorCode.CANCEL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponseDto> getResponse(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus()).body(new ErrorResponseDto(errorCode));
    }
}
