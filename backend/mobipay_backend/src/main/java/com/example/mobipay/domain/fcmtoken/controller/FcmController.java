package com.example.mobipay.domain.fcmtoken.controller;

import com.example.mobipay.domain.fcmtoken.dto.FcmSendDto;
import com.example.mobipay.domain.fcmtoken.dto.FcmTokenRequestDto;
import com.example.mobipay.domain.fcmtoken.dto.FcmTokenResponseDto;
import com.example.mobipay.domain.fcmtoken.error.FCMException;
import com.example.mobipay.domain.fcmtoken.service.FcmService;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.google.firebase.FirebaseException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm")
public class FcmController {

    private final FcmService fcmService;

    // 로그인 후 fcm 토큰을 받아서 mobiUser와 연관관계 설정
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/registertoken")
    public ResponseEntity<FcmTokenResponseDto> registerToken(@RequestBody @Valid FcmTokenRequestDto request,
                                                             @AuthenticationPrincipal CustomOAuth2User oauth2User) {
        fcmService.saveFcmToken(oauth2User, request);
        return ResponseEntity.ok(FcmTokenResponseDto.success());
    }

    // 로그인 하지 않고 fcm 토큰만 받아볼 수 있는 api
    @PostMapping("/registertoken-test")
    public ResponseEntity<FcmTokenResponseDto> registerToken(@RequestBody @Valid FcmTokenRequestDto request) {
        log.info("FcmTokenValue : " + request.getToken());
        return ResponseEntity.ok(FcmTokenResponseDto.success());
    }

    // 메시지 보내기 테스트용 api
    @PostMapping("/send")
    public ResponseEntity<?> pushMessage(@RequestBody @Valid FcmSendDto request) {

        try {
            fcmService.sendMessage(request);
        } catch (FirebaseException e) {
            throw new FCMException(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


}