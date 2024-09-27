package com.example.mobipay.formsignup.controller;

import com.example.mobipay.domain.mobiuser.error.MobiUserNotFoundException;
import com.example.mobipay.formsignup.dto.FormSignUpResponse;
import com.example.mobipay.global.authentication.dto.FormSignUpRequest;
import com.example.mobipay.global.authentication.service.SignUpServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/form-signup")
public class FormSignUpController {
    private static final String DEFAULT_IMAGE_URL = "https://avatars.githubusercontent.com/u/121084350?v=4";

    private final SignUpServiceImpl signUpServiceImpl;

    @PostMapping
    public ResponseEntity<?> testSignUp(@RequestBody @Valid FormSignUpRequest request) {
        try {
            signUpServiceImpl.checkIfMobiUserExists(request.getEmail());
            return ResponseEntity.badRequest().body(FormSignUpResponse.userAlreadyExists()); // 유저가 이미 존재하면 여기서 반환
        } catch (MobiUserNotFoundException e) { // 유저가 없으면 회원가입 진행
            signUpServiceImpl.signUp(request.getEmail(), request.getName(), request.getPhoneNumber(),
                    DEFAULT_IMAGE_URL);
            return ResponseEntity.ok().body(FormSignUpResponse.success());
        }
    }
}
