package com.example.mobipay.domain.fcmtoken.service;

import com.example.mobipay.domain.fcmtoken.dto.FcmSendDto;
import com.example.mobipay.domain.fcmtoken.dto.FcmTokenRequestDto;
import com.example.mobipay.domain.fcmtoken.entity.FcmToken;
import com.example.mobipay.domain.fcmtoken.repository.FcmTokenRepository;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.error.MobiUserNotFoundException;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidConfig.Priority;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class FcmServiceImpl implements FcmService {

    private final MobiUserRepository mobiUserRepository;
    private final FcmTokenRepository fcmTokenRepository;

    /**
     * 푸시 메시지 처리를 수행하는 비즈니스 로직
     *
     * @param fcmSendDto 모바일에서 전달받은 Object
     * @return 성공 시 true, 실패 시 false
     */
    @Override
    public void sendMessage(FcmSendDto fcmSendDto) throws FirebaseMessagingException {
        Notification notification = Notification.builder()
                .setTitle(fcmSendDto.getTitle())
                .setBody(fcmSendDto.getBody())
                .build();

        Message.Builder messageBuilder = Message.builder()
                .setToken(fcmSendDto.getToken())
                .setNotification(notification);

        if (fcmSendDto.getData() != null && !fcmSendDto.getData().isEmpty()) {
            messageBuilder.putAllData(fcmSendDto.getData());
            messageBuilder.setAndroidConfig(AndroidConfig.builder()
                    .setPriority(Priority.HIGH)
                    .build());
        }

        Message message = messageBuilder.build();
        String response = FirebaseMessaging.getInstance().send(message);

        log.debug("FCM 전송 성공: " + response);
    }

    @Override
    @Transactional
    public void saveFcmToken(CustomOAuth2User oauth2User, FcmTokenRequestDto request) {
        MobiUser mobiUser = mobiUserRepository.findById(oauth2User.getMobiUserId())
                .orElseThrow(MobiUserNotFoundException::new);
        FcmToken fcmToken = FcmToken.from(request.getToken());

        fcmTokenRepository.save(fcmToken);
        mobiUser.setFcmToken(fcmToken);
    }
}
