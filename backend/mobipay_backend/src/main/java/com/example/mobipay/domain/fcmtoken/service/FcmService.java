package com.example.mobipay.domain.fcmtoken.service;

import com.example.mobipay.domain.fcmtoken.dto.FcmSendDto;
import com.example.mobipay.domain.fcmtoken.dto.FcmTokenRequestDto;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.google.firebase.messaging.FirebaseMessagingException;
import java.io.IOException;
import org.springframework.stereotype.Service;

@Service
public interface FcmService {

    Boolean sendMessage(FcmSendDto fcmSendDto) throws IOException, FirebaseMessagingException;

    void saveFcmToken(CustomOAuth2User oauth2User, FcmTokenRequestDto request);
}
