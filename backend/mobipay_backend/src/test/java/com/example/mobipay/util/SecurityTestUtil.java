package com.example.mobipay.util;

import static org.mockito.Mockito.when;

import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityTestUtil {

    private static void setUpSecurityContext(CustomOAuth2User customOAuth2User) {

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customOAuth2User, null, customOAuth2User.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static void setUpMockUser(CustomOAuth2User customOAuth2User, Long mobiUserId) {
        when(customOAuth2User.getMobiUserId()).thenReturn(mobiUserId);
        SecurityTestUtil.setUpSecurityContext(customOAuth2User);
    }
}
