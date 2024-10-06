package com.example.mobipay.domain.postpayments.util;

import com.example.mobipay.domain.approvalwaiting.entity.ApprovalWaiting;
import com.example.mobipay.domain.approvalwaiting.error.ApprovalWaitingNotFoundException;
import com.example.mobipay.domain.approvalwaiting.repository.ApprovalWaitingRepository;
import com.example.mobipay.domain.merchant.entity.Merchant;
import com.example.mobipay.domain.merchant.error.MerchantNotFoundException;
import com.example.mobipay.domain.merchant.repository.MerchantRepository;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.error.MobiUserNotFoundException;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import com.example.mobipay.domain.ownedcard.repository.OwnedCardRepository;
import com.example.mobipay.domain.postpayments.dto.ApprovalPaymentRequest;
import com.example.mobipay.domain.postpayments.error.InvalidCardNoException;
import com.example.mobipay.domain.postpayments.error.NotEqualPaymentBalanceException;
import com.example.mobipay.domain.postpayments.error.OneTimeLimitExceedException;
import com.example.mobipay.domain.postpayments.error.RegisteredCardNotFoundException;
import com.example.mobipay.domain.postpayments.error.TransactionAlreadyApprovedException;
import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import com.example.mobipay.domain.registeredcard.entity.RegisteredCardId;
import com.example.mobipay.domain.registeredcard.repository.RegisteredCardRepository;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentValidator {

    private final MobiUserRepository mobiUserRepository;
    private final ApprovalWaitingRepository approvalWaitingRepository;
    private final OwnedCardRepository ownedCardRepository;
    private final RegisteredCardRepository registeredCardRepository;
    private final MerchantRepository merchantRepository;

    // oauth2User 검증
    public MobiUser validateOAuth2User(CustomOAuth2User oAuth2User) {
        Long oauth2UserId = oAuth2User.getMobiUserId();
        return mobiUserRepository.findById(oauth2UserId)
                .orElseThrow(MobiUserNotFoundException::new);
    }

    // approvalWaiting검증
    public ApprovalWaiting validateApprovalWaiting(ApprovalPaymentRequest request) {
        log.info("ApprovalPaymentRequest.getApprovalWaitingId(): {}", request.getApprovalWaitingId());

        ApprovalWaiting approvalWaiting = approvalWaitingRepository.findById(
                        request.getApprovalWaitingId())
                .orElseThrow(ApprovalWaitingNotFoundException::new);

        // ApprovalWaiting의 Approved가 true라면 이미 승인된 결제이므로 예외 발생
        if (approvalWaiting.getApproved()) {
            throw new TransactionAlreadyApprovedException();
        }

        if (!approvalWaiting.getPaymentBalance().equals(request.getPaymentBalance())) {
            throw new NotEqualPaymentBalanceException();
        }

        return approvalWaiting;
    }

    // cardNo 검증
    public OwnedCard validateCardNo(ApprovalPaymentRequest request) {
        return ownedCardRepository.findByCardNo(request.getCardNo())
                .orElseThrow(InvalidCardNoException::new);
    }

    // registeredCard 검증
    public RegisteredCard validateRegisteredCard(MobiUser mobiUser, OwnedCard ownedCard) {
        RegisteredCardId registeredCardId = RegisteredCardId.of(mobiUser.getId(), ownedCard.getId());
        return registeredCardRepository.findById(registeredCardId)
                .orElseThrow(RegisteredCardNotFoundException::new);
    }

    // merchant 검증
    public Merchant validateMerchant(ApprovalPaymentRequest request) {
        return merchantRepository.findById(request.getMerchantId())
                .orElseThrow(MerchantNotFoundException::new);
    }

    // 결제 금액과 일회 결제 한도 검증
    public void validateOneTimeLimit(ApprovalPaymentRequest request, RegisteredCard registeredCard) {
        Long paymentBalance = request.getPaymentBalance();
        Integer oneTimeLimit = registeredCard.getOneTimeLimit();
        if (paymentBalance > oneTimeLimit) {
            throw new OneTimeLimitExceedException();
        }
    }
}
