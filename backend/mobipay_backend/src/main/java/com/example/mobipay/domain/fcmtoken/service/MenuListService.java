package com.example.mobipay.domain.fcmtoken.service;

import static com.example.mobipay.domain.fcmtoken.enums.FcmTokenType.MENU_LIST;

import com.example.mobipay.domain.car.entity.Car;
import com.example.mobipay.domain.car.error.CarNotFoundException;
import com.example.mobipay.domain.car.repository.CarRepository;
import com.example.mobipay.domain.fcmtoken.dto.FcmSendDto;
import com.example.mobipay.domain.fcmtoken.dto.MenuListRequest;
import com.example.mobipay.domain.fcmtoken.error.FCMException;
import com.example.mobipay.domain.merchant.entity.Merchant;
import com.example.mobipay.domain.merchant.error.MerchantNotFoundException;
import com.example.mobipay.domain.merchant.repository.MerchantRepository;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.postpayments.error.InvalidMobiApiKeyException;
import com.google.firebase.messaging.FirebaseMessagingException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuListService {

    private final FcmService fcmServiceImpl;
    private final CarRepository carRepository;
    private final MerchantRepository merchantRepository;

    @Transactional(readOnly = true)
    public void sendMenuList(String mobiApiKey, MenuListRequest request) {

        Car car = findCarByNumber(request.getCarNumber()); // carNumber에 해당하는 차량이 DB에 없다면 404 Not Found
        Merchant merchant = findMerchantById(request.getMerchantId()); // merchantId로 merchant 조회. DB에 없다면 404 Not Found
        validateMobiApiKey(mobiApiKey, merchant); // merchant의 mobiapikey와 파라미터의 mobiApiKey가 같지 않다면 400 Bad Request

        Map<String, String> data = buildFcmData(merchant, request); // FCM 데이터 구성
        MobiUser mobiUser = car.getOwner();
        String token = mobiUser.getFcmToken().getValue();

        sendFcmMessage(token, data); // FCM 메시지 전송
    }

    private Car findCarByNumber(String carNumber) {
        return carRepository.findByNumber(carNumber)
                .orElseThrow(CarNotFoundException::new);
    }

    private Merchant findMerchantById(Long merchantId) {
        return merchantRepository.findById(merchantId)
                .orElseThrow(MerchantNotFoundException::new);
    }

    private void validateMobiApiKey(String mobiApiKey, Merchant merchant) {
        if (!mobiApiKey.equals(merchant.getApiKey())) {
            throw new InvalidMobiApiKeyException();
        }
    }

    private Map<String, String> buildFcmData(Merchant merchant, MenuListRequest request) {
        return Map.of(
                "type", MENU_LIST.getValue(),
                "title", "가맹점 메뉴 전달",
                "body", "가맹점 메뉴를 전달합니다.",
                "merchantName", merchant.getMerchantName(),
                "info", request.getInfo(),
                "roomId", request.getRoomId().toString()
        );
    }

    private void sendFcmMessage(String token, Map<String, String> data) {
        FcmSendDto fcmSendDto = new FcmSendDto(token, data);
        try {
            fcmServiceImpl.sendMessage(fcmSendDto);
        } catch (FirebaseMessagingException e) {
            throw new FCMException(e.getMessage());
        }
    }
}
