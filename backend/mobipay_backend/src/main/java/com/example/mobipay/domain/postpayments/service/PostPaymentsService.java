package com.example.mobipay.domain.postpayments.service;

import com.example.mobipay.domain.approvalwaiting.entity.ApprovalWaiting;
import com.example.mobipay.domain.approvalwaiting.repository.ApprovalWaitingRepository;
import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.car.error.CarNotFoundException;
import com.example.mobipay.domain.car.repository.CarRepository;
import com.example.mobipay.domain.cargroup.entity.CarGroup;
import com.example.mobipay.domain.cargroup.repository.CarGroupRepository;
import com.example.mobipay.domain.fcmtoken.dto.FcmSendDto;
import com.example.mobipay.domain.fcmtoken.service.FcmService;
import com.example.mobipay.domain.merchant.entity.Merchant;
import com.example.mobipay.domain.merchant.error.MerchantNotFoundException;
import com.example.mobipay.domain.merchant.repository.MerchantRepository;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.postpayments.dto.PaymentRequest;
import com.example.mobipay.domain.postpayments.error.InvalidMobiApiKeyException;
import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import com.example.mobipay.domain.registeredcard.repository.RegisteredCardRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostPaymentsService {

    private final MerchantRepository merchantRepository;
    private final CarRepository carRepository;
    private final CarGroupRepository carGroupRepository;
    private final RegisteredCardRepository registeredCardRepository;
    private final ApprovalWaitingRepository approvalWaitingRepository;
    private final FcmService fcmService;

    @Transactional
    public void sendRequestToCarGroup(PaymentRequest request, String mobiApiKey) {

        // mobiApiKey 검증
        Merchant merchant = validateApiKey(request.getMerchantId(), mobiApiKey);

        // Approval_Waiting 생성 및 저장
        ApprovalWaiting approvalWaiting = createApprovalWaiting(request.getPaymentBalance(),
                request.getCarNumber(),
                merchant);

        // carGroup 구성원에게 FCM 전송
        sendFcmToCarGroupMembers(request, approvalWaiting, merchant);
    }

    // Approval_Waiting 생성 및 관계 추가
    private ApprovalWaiting createApprovalWaiting(Long paymentBalance, String carNumber, Merchant merchant) {
        ApprovalWaiting approvalWaiting = ApprovalWaiting.of(paymentBalance);
        Car car = getCarByNumber(carNumber);
        approvalWaiting.addRelations(car, merchant);

        approvalWaitingRepository.save(approvalWaiting);
        return approvalWaiting;
    }

    // carNumber로 CAR 조회
    private Car getCarByNumber(String carNumber) {
        return carRepository.findByNumber(carNumber)
                .orElseThrow(CarNotFoundException::new);
    }

    // 가맹점이 올바른 mobiApiKey를 가지고 있는지 검증
    private Merchant validateApiKey(Long merchantId, String mobiApiKey) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(MerchantNotFoundException::new);

        if (!merchant.getApiKey().equals(mobiApiKey)) {
            throw new InvalidMobiApiKeyException();
        }
        return merchant;
    }

    // 그룹 멤버에게 FCM 푸시
    private void sendFcmToCarGroupMembers(PaymentRequest request, ApprovalWaiting approvalWaiting, Merchant merchant) {
        Car car = approvalWaiting.getCar();
        MobiUser owner = car.getOwner();
        List<CarGroup> carGroups = carGroupRepository.findByCarId(car.getId());

        carGroups.forEach(carGroup -> {
            // 차주인 경우
            if (isCarOwner(carGroup, owner)) {
                sendFcmToOwner(request, approvalWaiting, merchant, owner);
                return;
            }
            // 차주가 아닌 경우
            sendFcmToGroupMember(request, approvalWaiting, merchant, owner);
        });
    }

    // 차주인지 확인
    private boolean isCarOwner(CarGroup carGroup, MobiUser owner) {
        return carGroup.getMobiUserId().equals(owner.getId());
    }

    // 차주에게 FCM 푸시
    private void sendFcmToOwner(PaymentRequest request, ApprovalWaiting approvalWaiting,
                                Merchant merchant, MobiUser owner) {

        RegisteredCard registeredCard = getRegisteredCard(owner);
        // 자동결제 등록 카드가 없다면 실패 FCM 푸시
        if (registeredCard == null) {
            sendNoRegisteredCard(owner);
            return;
        }

        Map<String, String> data = buildFcmData(request, approvalWaiting, merchant,
                registeredCard.getOwnedCard().getCardNo(), true);
        sendFcmMessage(owner, data);
    }

    private void sendNoRegisteredCard(MobiUser owner) {
        FcmSendDto fcmSendDto = new FcmSendDto(owner.getFcmToken().getValue(), "자동결제 실패",
                "자동결제를 위한 카드가 등록되지 않았습니다.");
        sendFcmMessageWithErrorHandling(fcmSendDto);
    }

    // 그룹 멤버에게 FCM 푸시
    private void sendFcmToGroupMember(PaymentRequest request, ApprovalWaiting approvalWaiting,
                                      Merchant merchant, MobiUser owner) {
        Map<String, String> data = buildFcmData(request, approvalWaiting, merchant,
                null, false);
        sendFcmMessage(owner, data);
    }

    // FCM 메시지 데이터 생성
    private Map<String, String> buildFcmData(PaymentRequest request, ApprovalWaiting approvalWaiting,
                                             Merchant merchant, String cardNo, boolean autoPay) {
        return Map.of(
                "autoPay", String.valueOf(autoPay),
                "cardNo", cardNo,
                "approvalWaitingId", approvalWaiting.getId().toString(),
                "merchantId", merchant.getId().toString(),
                "paymentBalance", request.getPaymentBalance().toString(),
                "merchantName", merchant.getMerchantName(),
                "info", request.getInfo(),
                "lat", merchant.getLat().toString(),
                "lng", merchant.getLng().toString()
        );
    }

    // OwnerId로 RegisteredCard 가져오기
    private Optional<RegisteredCard> getRegisteredCard(Long ownerId) {
        return registeredCardRepository.findByMobiUserIdAndAutoPayStatus(ownerId, true);
    }

    // 등록된 카드 조회 및 처리
    private RegisteredCard getRegisteredCard(MobiUser owner) {
        Optional<RegisteredCard> optionalRegisteredCard = getRegisteredCard(owner.getId());
        if (optionalRegisteredCard.isEmpty()) {
            sendFailureNotification(owner);
            return null;
        }
        return optionalRegisteredCard.get();
    }

    // 자동결제 실패 FCM 푸시
    private void sendFailureNotification(MobiUser owner) {
        FcmSendDto fcmSendDto = new FcmSendDto(owner.getFcmToken().getValue(), "자동결제 실패", "자동결제를 위한 카드가 등록되지 않았습니다.");
        sendFcmMessageWithErrorHandling(fcmSendDto);
    }

    // FCM 메시지 전송
    private void sendFcmMessage(MobiUser user, Map<String, String> data) {
        FcmSendDto fcmSendDto = new FcmSendDto(user.getFcmToken().getValue(), data);
        sendFcmMessageWithErrorHandling(fcmSendDto);
    }

    // FCM 메시지 전송 및 에러 처리
    private void sendFcmMessageWithErrorHandling(FcmSendDto fcmSendDto) {
        try {
            fcmService.sendMessage(fcmSendDto);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
