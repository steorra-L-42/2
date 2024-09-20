package com.example.mobipay.oauth2.dto;

import java.util.Collection;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    public Long getMobiUserId() { // mobiUserId
        return null;
    }

    public String getEmail() { // email
        return null;
    }

    public String getPhoneNumber() { // phoneNumber
        return null;
    }

    public String getPicture() { // picture
        return null;
    }

    public String getRole() { // role
        return null;
    }
}
