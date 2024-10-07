package com.example.mobipay.domain.mobiuser.controller;

import com.example.mobipay.domain.mobiuser.dto.MyDataConsentResponse;
import com.example.mobipay.domain.mobiuser.service.MobiUserService;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class MobiUserController {

    private final MobiUserService mobiUserService;

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/mydata-consent")
    public ResponseEntity<MyDataConsentResponse> approveMyDataConsent(
            @AuthenticationPrincipal CustomOAuth2User oauth2User) {

        MyDataConsentResponse response = mobiUserService.approveMyDataConsent(oauth2User);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mydata-consent")
    public ResponseEntity<MyDataConsentResponse> getMyDataConsent(
            @AuthenticationPrincipal CustomOAuth2User oauth2User) {

        MyDataConsentResponse response = mobiUserService.getMyDataConsent(oauth2User);
        return ResponseEntity.ok(response);
    }
}
