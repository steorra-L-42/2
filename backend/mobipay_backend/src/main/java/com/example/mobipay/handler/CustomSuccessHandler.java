package com.example.mobipay.handler;

import static com.example.mobipay.oauth2.enums.TokenType.REFRESH;

import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.example.mobipay.oauth2.jwt.JWTUtil;
import com.example.mobipay.oauth2.repository.MobiUserRepository;
import com.example.mobipay.oauth2.util.CookieMethods;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final CookieMethods cookieMethods;
    private final MobiUserRepository mobiUserRepository;

    public CustomSuccessHandler(JWTUtil jwtUtil, CookieMethods cookieMethods, MobiUserRepository mobiUserRepository) {

        this.jwtUtil = jwtUtil;
        this.cookieMethods = cookieMethods;
        this.mobiUserRepository = mobiUserRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String email = customUserDetails.getEmail();
        String name = customUserDetails.getName();
        String phoneNumber = customUserDetails.getPhonenumber();
        String picture = customUserDetails.getPicture();
        String role = extractUserRole(authentication);

        String accessToken = jwtUtil.createAccessToken(email, name, phoneNumber, picture, role);
        String refreshToken = jwtUtil.createRefreshToken(email, name, phoneNumber, picture, role);

//        // Refresh 토큰 저장
//        saveRefreshToken(email, refreshToken);
//        addRefreshTokenToResponse(response, refreshToken);
//
//        // Redirect
//        redirectToTargetWithToken(request, response, accessToken);
    }

    private String extractUserRole(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.iterator().next().getAuthority();
    }

//    private void saveRefreshToken(String email, String refreshToken) {
//        MobiUser user = mobiUserRepository.findByEmail(email);
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, .getMessage()));
//
//        refreshTokenService.addRefreshToken(user, refreshToken);
//    }

    private void addRefreshTokenToResponse(HttpServletResponse response, String refreshToken) {
        Cookie refreshCookie = cookieMethods.createCookie(REFRESH.getType(), refreshToken);
        response.addCookie(refreshCookie);
    }

//    private void redirectToTargetWithToken(HttpServletRequest request, HttpServletResponse response, String accessToken)
//            throws IOException {
//        response.setStatus(HttpStatus.OK.value());
//        String targetUrl = getTargetUrl(accessToken);
//
//        getRedirectStrategy().sendRedirect(request, response, targetUrl);
//    }
//
//    // 액세스 토큰을 queryParam에 추가
//    private String getTargetUrl(String accessToken) {
//        return UriComponentsBuilder.fromUriString(appRedirectUri)
//                .queryParam("access", accessToken)
//                .build()
//                .toUriString();
//    }
}