package com.example.mobipay.domain.cancel.service;

import static com.example.mobipay.domain.fcmtoken.enums.FcmTokenType.INVITATION;
import static com.example.mobipay.domain.fcmtoken.enums.FcmTokenType.TRANSACTION_CANCEL;

import com.example.mobipay.domain.cancel.dto.MerchantTransactionItem;
import com.example.mobipay.domain.cancel.dto.MerchantTransactionResponse;
import com.example.mobipay.domain.cancel.dto.SsafyCancelTransactionRequest;
import com.example.mobipay.domain.cancel.dto.SsafyCancelTransactionResponse;
import com.example.mobipay.domain.cancel.error.CancelServerException;
import com.example.mobipay.domain.cancel.error.TransactionAlreadyCancelledException;
import com.example.mobipay.domain.cancel.error.TransactionNotBelongToMerchantException;
import com.example.mobipay.domain.fcmtoken.dto.FcmSendDto;
import com.example.mobipay.domain.fcmtoken.error.FCMException;
import com.example.mobipay.domain.fcmtoken.service.FcmService;
import com.example.mobipay.domain.invitation.entity.Invitation;
import com.example.mobipay.domain.merchant.entity.Merchant;
import com.example.mobipay.domain.merchant.error.MerchantNotFoundException;
import com.example.mobipay.domain.merchant.repository.MerchantRepository;
import com.example.mobipay.domain.merchanttransaction.entity.MerchantTransaction;
import com.example.mobipay.domain.merchanttransaction.error.MerchantTransactionNotFoundException;
import com.example.mobipay.domain.merchanttransaction.repository.MerchantTransactionRepository;
import com.example.mobipay.domain.postpayments.error.InvalidMobiApiKeyException;
import com.example.mobipay.util.RestClientUtil;
import com.google.firebase.messaging.FirebaseMessagingException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CancelService {

    @Value("${ssafy.api.key}")
    private String ssafyApiKey;

    private final MerchantTransactionRepository merchantTransactionRepository;
    private final MerchantRepository merchantRepository;
    private final RestClientUtil restClientUtil;
    private final FcmService fcmServiceImpl;

    public MerchantTransactionResponse getTransactions(String mobiApiKey, Long merchantId) {

        validateMobiApiKey(mobiApiKey, merchantId);

        List<MerchantTransactionItem> transactionItems = merchantTransactionRepository.findAllByMerchantId(merchantId).stream()
                .map(MerchantTransactionItem::from)
                .collect(Collectors.toList());

        return MerchantTransactionResponse.of(transactionItems);
    }


    @Transactional
    public void cancelTransaction(String mobiApiKey, Long merchantId, Long transactionUniqueNo) {

        validateMobiApiKey(mobiApiKey, merchantId);

        MerchantTransaction merchantTransaction = merchantTransactionRepository.findByTransactionUniqueNo(transactionUniqueNo)
                .orElseThrow(MerchantTransactionNotFoundException::new);

        validateMerchantTransaction(merchantTransaction, merchantId);
        validateNotCancelledTransaction(merchantTransaction);

        // 취소 요청
        SsafyCancelTransactionRequest ssafyCancelTransactionRequest = SsafyCancelTransactionRequest.builder()
                .apiName("deleteTransaction")
                .apiKey(ssafyApiKey)
                .userKey(merchantTransaction.getRegisteredCard().getMobiUser().getSsafyUser().getUserKey())
                .cardNo(merchantTransaction.getRegisteredCard().getOwnedCard().getCardNo())
                .cvc(merchantTransaction.getRegisteredCard().getOwnedCard().getCvc())
                .transactionUniqueNo(transactionUniqueNo)
                .build();

        try {
            restClientUtil.cancelTransaction(ssafyCancelTransactionRequest, SsafyCancelTransactionResponse.class);
        }catch (Exception e) {
            throw new CancelServerException();
        }

        // fcm 전송
        Map<String, String> data = Map.of(
                "type", TRANSACTION_CANCEL.getValue(),
                "title", "결제 취소",
                "body", "결제가 취소되었습니다.",
                "merchantName", merchantTransaction.getMerchant().getMerchantName(),
                "paymentBalance", merchantTransaction.getPaymentBalance().toString(),
                "cardNo", merchantTransaction.getRegisteredCard().getOwnedCard().getCardNo()
        );
        sendCancelMessage(merchantTransaction, data);

        merchantTransaction.cancel();
    }

    private void validateMobiApiKey(String mobiApiKey, Long merchantId) {
        if(mobiApiKey == null || mobiApiKey.isEmpty()) {
            throw new InvalidMobiApiKeyException();
        }
        if(merchantId == null) {
            throw new MerchantNotFoundException();
        }

        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(MerchantNotFoundException::new);

        boolean inValid = !mobiApiKey.equals(merchant.getApiKey());
        if(inValid) {
            throw new InvalidMobiApiKeyException();
        }
    }

    // 해당 가맹점의 거래내역이 아닌 경우
    private void  validateMerchantTransaction(MerchantTransaction merchantTransaction, Long merchantId) {
        if(!Objects.equals(merchantTransaction.getMerchant().getId(), merchantId)) {
            throw new TransactionNotBelongToMerchantException();
        }
    }

    // 이미 취소된 거래인 경우
    private void validateNotCancelledTransaction(MerchantTransaction merchantTransaction) {
        if(merchantTransaction.getCancelled()) {
            throw new TransactionAlreadyCancelledException();
        }
    }

    private void sendCancelMessage(MerchantTransaction merchantTransaction, Map<String, String> data) {
        // 초대 메시지 전송
        String token = merchantTransaction.getRegisteredCard().getMobiUser().getFcmToken().getValue();
        FcmSendDto fcmSendDto = new FcmSendDto(token, data);

        try {
            fcmServiceImpl.sendMessage(fcmSendDto);
        } catch (FirebaseMessagingException e) {
            throw new FCMException(e.getMessage());
        }
    }

}
