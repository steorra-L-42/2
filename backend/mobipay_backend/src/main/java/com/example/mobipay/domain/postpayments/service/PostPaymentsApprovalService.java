package com.example.mobipay.domain.postpayments.service;

import com.example.mobipay.domain.approvalwaiting.entity.ApprovalWaiting;
import com.example.mobipay.domain.approvalwaiting.error.ApprovalWaitingNotFoundException;
import com.example.mobipay.domain.approvalwaiting.repository.ApprovalWaitingRepository;
import com.example.mobipay.domain.merchant.entity.Merchant;
import com.example.mobipay.domain.merchant.error.MerchantNotFoundException;
import com.example.mobipay.domain.merchant.repository.MerchantRepository;
import com.example.mobipay.domain.merchanttransaction.entity.MerchantTransaction;
import com.example.mobipay.domain.merchanttransaction.repository.MerchantTransactionRepository;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.error.MobiUserNotFoundException;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import com.example.mobipay.domain.ownedcard.repository.OwnedCardRepository;
import com.example.mobipay.domain.postpayments.dto.ApprovalPaymentRequest;
import com.example.mobipay.domain.postpayments.dto.ApprovalPaymentResponse;
import com.example.mobipay.domain.postpayments.dto.cardtransaction.CardTransactionRequest;
import com.example.mobipay.domain.postpayments.dto.cardtransaction.CardTransactionResponse;
import com.example.mobipay.domain.postpayments.error.CardTransactionServerError;
import com.example.mobipay.domain.postpayments.error.InvalidCardNoException;
import com.example.mobipay.domain.postpayments.error.InvalidPaymentBalanceException;
import com.example.mobipay.domain.postpayments.error.RegisteredCardNotFoundException;
import com.example.mobipay.domain.postpayments.error.TransactionAlreadyApprovedException;
import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import com.example.mobipay.domain.registeredcard.entity.RegisteredCardId;
import com.example.mobipay.domain.registeredcard.repository.RegisteredCardRepository;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.example.mobipay.util.RestClientUtil;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostPaymentsApprovalService {

    private static final String CARD_TRANSACTION_REQUEST = "createCreditCardTransaction";

    private final MobiUserRepository mobiUserRepository;
    private final ApprovalWaitingRepository approvalWaitingRepository;
    private final OwnedCardRepository ownedCardRepository;
    private final RegisteredCardRepository registeredCardRepository;
    private final MerchantRepository merchantRepository;
    private final MerchantTransactionRepository merchantTransactionRepository;
    private final RestClientUtil restClientUtil;

    @Value("${ssafy.api.key}")
    private String ssafyApiKey;

    // Android로부터 결제요청이 온 경우 결제 로직 시작
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public ApprovalPaymentResponse processPaymentApproval(ApprovalPaymentRequest request, CustomOAuth2User oAuth2User) {

        // oauth2User 검증
        MobiUser mobiUser = validateOAuth2User(oAuth2User);

        // request에서 approved가 false이면 return
        if (!request.getApproved()) {
            return ApprovalPaymentResponse.from(request);
        }

        // approvalWaiting 검증
        ApprovalWaiting approvalWaiting = validateApprovalWaiting(request);
        // cardNo 및 registeredCard 검증
        OwnedCard ownedCard = validateCardNo(request);
        RegisteredCard registeredCard = validateRegisteredCard(mobiUser, ownedCard);
        // merchant 검증
        Merchant merchant = validateMerchant(request);
        // SSAFY_API 결제 진행 요청
        processTransaction(request, mobiUser, ownedCard, registeredCard, merchant, approvalWaiting);

        return ApprovalPaymentResponse.from(request);
    }

    // oauth2User 검증
    private MobiUser validateOAuth2User(CustomOAuth2User oAuth2User) {
        Long oauth2UserId = oAuth2User.getMobiUserId();
        return mobiUserRepository.findById(oauth2UserId)
                .orElseThrow(MobiUserNotFoundException::new);
    }

    // approvalWaiting검증
    private ApprovalWaiting validateApprovalWaiting(ApprovalPaymentRequest request) {
        ApprovalWaiting approvalWaiting = approvalWaitingRepository.findById(
                        request.getApprovalWaitingId())
                .orElseThrow(ApprovalWaitingNotFoundException::new);

        // ApprovalWaiting의 Approved가 true라면 이미 승인된 결제이므로 예외 발생
        if (approvalWaiting.getApproved()) {
            throw new TransactionAlreadyApprovedException();
        }

        if (!approvalWaiting.getPaymentBalance().equals(request.getPaymentBalance())) {
            throw new InvalidPaymentBalanceException();
        }

        return approvalWaiting;
    }

    // cardNo 검증
    private OwnedCard validateCardNo(ApprovalPaymentRequest request) {
        return ownedCardRepository.findByCardNo(request.getCardNo())
                .orElseThrow(InvalidCardNoException::new);
    }

    // registeredCard 검증
    private RegisteredCard validateRegisteredCard(MobiUser mobiUser, OwnedCard ownedCard) {
        RegisteredCardId registeredCardId = RegisteredCardId.of(mobiUser.getId(), ownedCard.getId());
        return registeredCardRepository.findById(registeredCardId)
                .orElseThrow(RegisteredCardNotFoundException::new);
    }

    // merchant 검증
    private Merchant validateMerchant(ApprovalPaymentRequest request) {
        return merchantRepository.findById(request.getMerchantId())
                .orElseThrow(MerchantNotFoundException::new);
    }

    // SSAFY_API 결제 진행 및 응답 처리
    private void processTransaction(ApprovalPaymentRequest request, MobiUser mobiUser, OwnedCard ownedCard,
                                    RegisteredCard registeredCard, Merchant merchant, ApprovalWaiting approvalWaiting) {

        ResponseEntity<CardTransactionResponse> response = getCardTransactionResponse(request, mobiUser, ownedCard);

        // 결제 승인 성공 시 MerchantTransaction 생성 및 저장
        boolean responseSuccess = response.getStatusCode().is2xxSuccessful();
        if (responseSuccess) {
            createMerchantTransaction(request, response, registeredCard, merchant);
            changeApprovedStatus(approvalWaiting);
            return;
        }
        // 결제 실패 처리
        handleTransactionFailure(response);
    }

    // SSAFY_API 결제 요청
    private ResponseEntity<CardTransactionResponse> getCardTransactionResponse(
            ApprovalPaymentRequest request, MobiUser mobiUser, OwnedCard ownedCard) {
        CardTransactionRequest cardTransactionRequest = new CardTransactionRequest(
                CARD_TRANSACTION_REQUEST,
                ssafyApiKey,
                mobiUser.getSsafyUser().getUserKey(),
                ownedCard,
                request
        );

        return restClientUtil.processCardTransaction(cardTransactionRequest, CardTransactionResponse.class);
    }

    // MerchantTransaction 생성 및 저장
    private void createMerchantTransaction(ApprovalPaymentRequest request,
                                           ResponseEntity<CardTransactionResponse> response,
                                           RegisteredCard registeredCard,
                                           Merchant merchant) {
        MerchantTransaction merchantTransaction = MerchantTransaction.of(request, response);
        merchantTransaction.addRelations(registeredCard, merchant);
        merchantTransactionRepository.save(merchantTransaction);
    }

    // ApprovalWaiting의 approved 상태 변경
    private void changeApprovedStatus(ApprovalWaiting approvalWaiting) {
        approvalWaiting.activateApproved();
        approvalWaitingRepository.save(approvalWaiting);
    }

    // 결제 실패 시 처리
    private void handleTransactionFailure(ResponseEntity<CardTransactionResponse> response) {
        CardTransactionResponse responseBody = response.getBody();
        log.info("cardTransaction ResponseCode : {}", responseBody.getResponseCode());

        if (responseBody.getResponseCode().contains("H1") || responseBody.getResponseCode().contains("Q")) {
            throw new CardTransactionServerError();
        }
    }
}