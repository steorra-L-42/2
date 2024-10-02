package com.example.mobipay.domain.fcmtoken.service;

import com.example.mobipay.domain.fcmtoken.dto.FcmSendDto;
import com.example.mobipay.domain.fcmtoken.dto.FcmTokenRequestDto;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.stereotype.Service;

@Service
public interface FcmService {

    void sendMessage(FcmSendDto fcmSendDto) throws FirebaseMessagingException;

    void saveFcmToken(CustomOAuth2User oauth2User, FcmTokenRequestDto request);
}
