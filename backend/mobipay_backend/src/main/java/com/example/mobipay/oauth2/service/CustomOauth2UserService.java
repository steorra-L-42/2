package com.example.mobipay.oauth2.service;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.example.mobipay.oauth2.dto.KakaoResponse;
import com.example.mobipay.oauth2.dto.OAuth2Response;
import com.example.mobipay.oauth2.dto.UserDTO;
import com.example.mobipay.oauth2.repository.MobiUserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final MobiUserRepository mobiUserRepository;

    public CustomOauth2UserService(MobiUserRepository mobiUserRepository) {
        this.mobiUserRepository = mobiUserRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("kakao")) {

            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

//        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        MobiUser existData = mobiUserRepository.findByEmail(oAuth2Response.getEmail());

        if (existData == null) {
            MobiUser mobiUser = MobiUser.create(
                    oAuth2Response.getEmail(),
                    oAuth2Response.getname(),
                    oAuth2Response.getPhoneNumber(),
                    oAuth2Response.getPicture()
            );
            mobiUserRepository.save(mobiUser);

            UserDTO userDTO = new UserDTO();
            //        userDTO.setUsername(username);
            userDTO.setEmail(oAuth2Response.getEmail());
            userDTO.setName(oAuth2Response.getname());
            userDTO.setPicture(oAuth2Response.getPicture());
            userDTO.setPhonenumber(oAuth2Response.getPhoneNumber());
            userDTO.setRole("User");

            return new CustomOAuth2User(userDTO);
        } else {
//            existData.setName(oAuth2Response.getname());
//            existData.setPhoneNumber(oAuth2Response.getPhoneNumber());
//            existData.setPicture(oAuth2Response.getPicture());

            // 기존 데이터 업데이트 (필요 시)
            mobiUserRepository.save(existData);

            // 기존 사용자 정보를 DTO로 변환하여 반환
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(existData.getEmail());
            userDTO.setName(existData.getName());
            userDTO.setPicture(existData.getPicture());
            userDTO.setPhonenumber(existData.getPhoneNumber());
            userDTO.setRole(existData.getRole().name());

            return new CustomOAuth2User(userDTO);
        }

    }
}
