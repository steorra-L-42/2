package com.example.mobipay.oauth2.service;

import com.example.mobipay.domain.kakaotoken.entity.KakaoToken;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.mobiuser.enums.Role;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.example.mobipay.oauth2.dto.KakaoResponse;
import com.example.mobipay.oauth2.dto.OAuth2Response;
import com.example.mobipay.oauth2.dto.UserDTO;
import com.example.mobipay.oauth2.repository.AuthTokenRepository;
import com.example.mobipay.oauth2.repository.MobiUserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final MobiUserRepository mobiUserRepository;
    private final AuthTokenRepository authTokenRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String authValue = userRequest.getAccessToken().getTokenValue();
//        String freshValue = userRequest.getAccessToken().getRef
        System.out.println(authValue);

        OAuth2Response oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());

        // DB 저장 구현
        // 전달받은 데이터에서 username으로 지칭할 수 있는 것이 없기에 별도의 메소드를 구현한다.
//        String authId = AuthIdCreator.getAuthId(oAuth2Response);
        //MobiUser가 null값인 것을 방지하기 위한 장치 Optional
        String email = oAuth2Response.getEmail();
        Optional<MobiUser> optionalUser = mobiUserRepository.findByAuthIdAndActivated(email, true);

        String name = oAuth2Response.getName();
        String picture = oAuth2Response.getPicture();
        String phonenumber = oAuth2Response.getPhoneNumber();

        // 기존에 존재하는 유저는 handleExistingUser
        // 새로 가입한 유저는 handleNewUser
        return optionalUser.map(existUser -> handleExistingUser(existUser, picture))
                .orElseGet(() -> handleNewUser(email, authValue, freshValue, name, picture, phonenumber));

    }

    //    트랜젝션 격리수준 최고(무결성 동시성제어)
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CustomOAuth2User handleNewUser(String email, String authValue, String freshValue, String name,
                                          String picture,
                                          String phonenumber) {
        MobiUser mobiUser = createNewUser(email, name, picture, phonenumber);
        KakaoToken authToken = createAuthToken(authValue, freshValue, mobiUser);

        mobiUserRepository.save(mobiUser);
        authTokenRepository.save(authToken);

        UserDTO userDTO = createUserDTO(mobiUser);

        return new CustomOAuth2User(userDTO);
    }

    private MobiUser createNewUser(String email, String name, String picture, String phonenumber) {
        return MobiUser.builder()
                .email(email)
                .name(name)
                .picture(picture)
                .phoneNumber(phonenumber)
                .build();
    }

    private KakaoToken createAuthToken(String authValue, String freshValue, MobiUser mobiUser) {
        return KakaoToken.builder()
                .accessValue(authValue)
                .refreshValue(freshValue)
                .mobiUser(mobiUser)
                .build();
    }

    private UserDTO createUserDTO(MobiUser mobiUser) {
        String role = Optional.ofNullable(mobiUser.getRole())
                .map(Role::name)
                .orElse(Role.USER.name());

        return UserDTO.builder()
                .userId(mobiUser.getId())
                .email(mobiUser.getEmail())
                .name(mobiUser.getName())
                .phonenumber(mobiUser.getPhoneNumber())
                .role(role)
                .build();

    }

    // 기존 유저
    private CustomOAuth2User handleExistingUser(MobiUser existUser, String picture) {
        existUser.updatePicture(picture);
        mobiUserRepository.save(existUser);

        UserDTO userDTO = UserDTO.builder()
                .email(existUser.getEmail())
                .name(existUser.getName())
                .picture(existUser.getPicture())
                .phonenumber(existUser.getPhoneNumber())
                .build();

        return new CustomOAuth2User(userDTO);
    }
}
