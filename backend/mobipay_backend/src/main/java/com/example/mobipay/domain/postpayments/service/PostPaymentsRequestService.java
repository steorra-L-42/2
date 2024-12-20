package com.example.mobipay.domain.postpayments.service;

import static com.example.mobipay.domain.fcmtoken.enums.FcmTokenType.AUTO_PAY_FAILED;
import static com.example.mobipay.domain.fcmtoken.enums.FcmTokenType.TRANSACTION_REQUEST;

import com.example.mobipay.domain.approvalwaiting.entity.ApprovalWaiting;
import com.example.mobipay.domain.approvalwaiting.service.ApprovalWaitingService;
import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.cargroup.entity.CarGroup;
import com.example.mobipay.domain.cargroup.repository.CarGroupRepository;
import com.example.mobipay.domain.fcmtoken.dto.FcmSendDto;
import com.example.mobipay.domain.fcmtoken.error.FCMException;
import com.example.mobipay.domain.fcmtoken.service.FcmService;
import com.example.mobipay.domain.merchant.entity.Merchant;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.postpayments.dto.ApprovalWaitingResponse;
import com.example.mobipay.domain.postpayments.dto.PaymentRequest;
import com.example.mobipay.domain.postpayments.dto.PaymentResponse;
import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import com.example.mobipay.domain.registeredcard.repository.RegisteredCardRepository;
import com.google.firebase.FirebaseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostPaymentsRequestService {

    private final ApprovalWaitingService approvalWaitingService;
    private final CarGroupRepository carGroupRepository;
    private final RegisteredCardRepository registeredCardRepository;
    private final FcmService fcmService;

    public PaymentResponse sendRequestToCarGroup(PaymentRequest request, String mobiApiKey) {

        // ApprovalWaiting 생성
        ApprovalWaitingResponse approvalWaitingResponse = approvalWaitingService.getApprovalWaiting(request,
                mobiApiKey);
        ApprovalWaiting approvalWaiting = approvalWaitingResponse.getApprovalWaiting();
        Merchant merchant = approvalWaitingResponse.getMerchant();

        // carGroup 구성원에게 FCM 전송
        Car car = approvalWaiting.getCar();
        sendFcmToCarGroupMembers(request, approvalWaiting, merchant, car);

        return PaymentResponse.of(approvalWaiting, car, merchant, request);
    }

    // 그룹 멤버에게 FCM 푸시
    private void sendFcmToCarGroupMembers(PaymentRequest request, ApprovalWaiting approvalWaiting, Merchant merchant,
                                          Car car) {
        MobiUser owner = car.getOwner();
        List<CarGroup> carGroups = carGroupRepository.findByCarId(car.getId());

        carGroups.forEach(carGroup -> {
            // 차주인 경우
            if (isCarOwner(carGroup, owner)) {
                sendFcmToOwner(request, approvalWaiting, merchant, owner, car);
                return;
            }
            // 차주가 아닌 경우
            sendFcmForManualPay(request, approvalWaiting, merchant, carGroup.getMobiUser());
        });
    }

    // 차주인지 확인
    private boolean isCarOwner(CarGroup carGroup, MobiUser owner) {
        return carGroup.getMobiUserId().equals(owner.getId());
    }

    // 차주에게 FCM 푸시
    private void sendFcmToOwner(PaymentRequest request, ApprovalWaiting approvalWaiting,
                                Merchant merchant, MobiUser owner, Car car) {
        if (car.getAutoPayStatus()) {
            Optional<RegisteredCard> optionalRegisteredCard = getRegisteredCard(owner.getId());
            // 자동결제 등록 카드가 없다면 실패 FCM 푸시 -> 수동결제 요청 FCM 푸시
            if (optionalRegisteredCard.isEmpty()) {
                sendFcmNoRegisteredCard(owner);
                sendFcmForManualPay(request, approvalWaiting, merchant, owner);
                return;
            }
            // 자동결제 등록 카드가 있다면 자동결제 요청 FCM 푸시
            RegisteredCard registeredCard = optionalRegisteredCard.get();
            sendFcmForAutoPay(request, approvalWaiting, merchant, owner, registeredCard);
            return;
        }
        // 자동차 autoPay가 꺼져있으면 수동결제 요청 FCM 푸시
        sendFcmForManualPay(request, approvalWaiting, merchant, owner);
    }

    // OwnerId로 RegisteredCard 가져오기
    private Optional<RegisteredCard> getRegisteredCard(Long ownerId) {
        return registeredCardRepository.findByMobiUserIdAndAutoPayStatus(ownerId, true);
    }

    // 자동결제 등록 카드가 없다는 FCM 푸시
    private void sendFcmNoRegisteredCard(MobiUser owner) {
        Map<String, String> data = Map.of(
                "title", "자동결제 실패",
                "body", "자동결제를 위한 카드가 등록되지 않았습니다.",
                "type", AUTO_PAY_FAILED.getValue());

        FcmSendDto fcmSendDto = new FcmSendDto(owner.getFcmToken().getValue(), data);
        sendFcmMessageWithErrorHandling(fcmSendDto);
    }

    // 수동결제 FCM 푸시
    private void sendFcmForManualPay(PaymentRequest request, ApprovalWaiting approvalWaiting,
                                     Merchant merchant, MobiUser mobiUser) {
        Map<String, String> data = buildFcmDataForTransaction(request, approvalWaiting, merchant,
                null, false);
        sendFcmMessage(mobiUser, data);
    }

    // 자동결제 FCM 푸시
    private void sendFcmForAutoPay(PaymentRequest request, ApprovalWaiting approvalWaiting, Merchant merchant,
                                   MobiUser owner, RegisteredCard registeredCard) {
        Map<String, String> data = buildFcmDataForTransaction(request, approvalWaiting, merchant,
                registeredCard.getOwnedCard().getCardNo(), true);
        sendFcmMessage(owner, data);
    }

    // FCM 메시지 데이터 생성
    private Map<String, String> buildFcmDataForTransaction(PaymentRequest request, ApprovalWaiting approvalWaiting,
                                                           Merchant merchant, String cardNo, boolean autoPay) {
        Map<String, String> fcmData = new HashMap<>();
        fcmData.put("autoPay", String.valueOf(autoPay));
        fcmData.put("approvalWaitingId", approvalWaiting.getId().toString());
        fcmData.put("merchantId", merchant.getId().toString());
        fcmData.put("paymentBalance", request.getPaymentBalance().toString());
        fcmData.put("merchantName", merchant.getMerchantName());
        fcmData.put("info", request.getInfo());
        fcmData.put("lat", merchant.getLat().toString());
        fcmData.put("lng", merchant.getLng().toString());
        fcmData.put("type", TRANSACTION_REQUEST.getValue());

        // cardNo가 null이 아니면 추가
        if (cardNo != null) {
            fcmData.put("cardNo", cardNo);
        }

        return Collections.unmodifiableMap(fcmData); // 불변 맵으로 반환
    }

    // FCM 메시지 전송
    private void sendFcmMessage(MobiUser mobiUser, Map<String, String> data) {
        FcmSendDto fcmSendDto = new FcmSendDto(mobiUser.getFcmToken().getValue(), data);
        sendFcmMessageWithErrorHandling(fcmSendDto);
    }

    // FCM 메시지 전송 및 에러 처리
    private void sendFcmMessageWithErrorHandling(FcmSendDto fcmSendDto) {
        try {
            fcmService.sendMessage(fcmSendDto);
        } catch (FirebaseException e) {
            throw new FCMException(e.getMessage());
        }
    }
}
