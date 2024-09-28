package com.example.mobipay.oauth2.service;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.example.mobipay.oauth2.dto.KakaoResponse;
import com.example.mobipay.oauth2.dto.OAuth2Response;
import com.example.mobipay.oauth2.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final MobiUserRepository mobiUserRepository;
    private final UserService userService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Spring Security에서 제공하는 기본 OAuth2UserService를 사용하여 OAuth2User 객체를 가져옴
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // OAuth2 제공자로부터 받은 사용자 정보로 사용자 객체를 생성
        OAuth2Response oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        String email = oAuth2Response.getEmail();
        String name = oAuth2Response.getName();
        String phoneNumber = oAuth2Response.getPhoneNumber();
        String picture = oAuth2Response.getPicture();

        // 사용자 정보를 저장하거나 업데이트
        MobiUser mobiUser = userService.createUser(email, name, phoneNumber, picture);

        // MobiUser를 UserDTO로 변환
        UserDTO userDTO = convertToUserDTO(mobiUser);

        // Spring Security에서 사용할 CustomOAuth2User 객체로 변환하여 반환
        return new CustomOAuth2User(userDTO);
    }

    // MobiUser에서 UserDTO로 변환하는 메서드
    private UserDTO convertToUserDTO(MobiUser mobiUser) {
        return UserDTO.builder()
                .email(mobiUser.getEmail())
                .name(mobiUser.getName())
                .phonenumber(mobiUser.getPhoneNumber())
                .picture(mobiUser.getPicture())
                .role(mobiUser.getRole().name()) // Role이 존재한다면 추가
                .build();
    }
}
