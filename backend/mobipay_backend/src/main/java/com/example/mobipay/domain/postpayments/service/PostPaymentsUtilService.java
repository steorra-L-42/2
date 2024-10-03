package com.example.mobipay.domain.postpayments.service;

import com.example.mobipay.domain.merchant.error.MerchantNotFoundException;
import com.example.mobipay.domain.merchanttransaction.entity.MerchantTransaction;
import com.example.mobipay.domain.merchanttransaction.error.MerchantTransactionNotFoundException;
import com.example.mobipay.domain.merchanttransaction.repository.MerchantTransactionRepository;
import com.example.mobipay.domain.mobiuser.error.MobiUserNotFoundException;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import com.example.mobipay.domain.ownedcard.error.OwnedCardNotFoundException;
import com.example.mobipay.domain.postpayments.dto.ReceiptResponse;
import com.example.mobipay.domain.postpayments.dto.historyresponse.HistoryListResponse;
import com.example.mobipay.domain.postpayments.error.ReceiptUserMismatchException;
import com.example.mobipay.domain.postpayments.error.RegisteredCardNotFoundException;
import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import com.example.mobipay.global.authentication.error.CardProductNotFoundException;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostPaymentsUtilService {

    private final MerchantTransactionRepository merchantTransactionRepository;
    private final MobiUserRepository mobiUserRepository;

    @Transactional(readOnly = true)
    public ReceiptResponse getReceipt(Long transactionUniqueNo, CustomOAuth2User oAuth2User) {

        MerchantTransaction merchantTransaction = getMerchantTransaction(transactionUniqueNo);
        validateUser(oAuth2User, merchantTransaction);

        return buildReceiptResponse(merchantTransaction);
    }

    // MerchantTransaction 얻기
    private MerchantTransaction getMerchantTransaction(Long transactionUniqueNo) {
        return merchantTransactionRepository.findByTransactionUniqueNo(
                        transactionUniqueNo)
                .orElseThrow(MerchantTransactionNotFoundException::new);
    }

    // MerchantTransaction의 유저와, JWT 유저가 같은지 검증
    private void validateUser(CustomOAuth2User oAuth2User, MerchantTransaction merchantTransaction) {
        Long mobiUserId = merchantTransaction.getRegisteredCard().getMobiUserId();
        if (!mobiUserId.equals(oAuth2User.getMobiUserId())) {
            throw new ReceiptUserMismatchException();
        }
    }

    // null 검증 및 recriptResponse 생성
    private ReceiptResponse buildReceiptResponse(MerchantTransaction merchantTransaction) {
        RegisteredCard registeredCard = Optional.ofNullable(merchantTransaction.getRegisteredCard())
                .orElseThrow(RegisteredCardNotFoundException::new);

        OwnedCard ownedCard = Optional.ofNullable(registeredCard.getOwnedCard())
                .orElseThrow(OwnedCardNotFoundException::new);

        Optional.ofNullable(ownedCard.getCardProduct())
                .orElseThrow(CardProductNotFoundException::new);

        Optional.ofNullable(merchantTransaction.getMerchant())
                .orElseThrow(MerchantNotFoundException::new);

        return ReceiptResponse.from(merchantTransaction);
    }

    @Transactional(readOnly = true)
    public HistoryListResponse getHistories(CustomOAuth2User oAuth2User) {

        mobiUserRepository.findById(oAuth2User.getMobiUserId())
                .orElseThrow(MobiUserNotFoundException::new);

        List<MerchantTransaction> mobiUserTransactions = merchantTransactionRepository.findByMobiUserId(
                oAuth2User.getMobiUserId());

        return HistoryListResponse.from(mobiUserTransactions);
    }
}
