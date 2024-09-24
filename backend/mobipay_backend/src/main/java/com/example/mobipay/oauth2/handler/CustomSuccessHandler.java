package com.example.mobipay.oauth2.handler;

import static com.example.mobipay.oauth2.enums.TokenType.REFRESH;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.example.mobipay.oauth2.jwt.JWTUtil;
import com.example.mobipay.oauth2.repository.MobiUserRepository;
import com.example.mobipay.oauth2.service.RefreshTokenService;
import com.example.mobipay.oauth2.util.CookieMethods;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final CookieMethods cookieMethods;
    private final MobiUserRepository mobiUserRepository;
    private final RefreshTokenService refreshTokenService;


    @Value("${app.redirect.uri}")
    private String appRedirectUri;

    public CustomSuccessHandler(JWTUtil jwtUtil, CookieMethods cookieMethods, MobiUserRepository mobiUserRepository,
                                RefreshTokenService refreshTokenService) {

        this.jwtUtil = jwtUtil;
        this.cookieMethods = cookieMethods;
        this.mobiUserRepository = mobiUserRepository;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String email = customUserDetails.getEmail();
        Long userId = customUserDetails.getUserId();
        String name = customUserDetails.getName();
        String phoneNumber = customUserDetails.getPhonenumber();
        String picture = customUserDetails.getPicture();
        String role = extractUserRole(authentication);

//        String accessToken = jwtUtil.createAccessToken(email, name, phoneNumber, picture);
        String refreshToken = jwtUtil.createRefreshToken(email, name, phoneNumber, picture);
//        System.out.println("accessToken" + accessToken);
        System.out.println("refreshToken" + refreshToken);

        // Refresh 토큰 저장
        saveRefreshToken(userId, refreshToken);
        addRefreshTokenToResponse(response, refreshToken);

        // Redirect
//        redirectToTargetWithToken(request, response, accessToken);
    }

    private String extractUserRole(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.iterator().next().getAuthority();
    }

    private void saveRefreshToken(Long userId, String refreshToken) {
        MobiUser mobiUser = mobiUserRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        HttpStatus.BAD_REQUEST.getReasonPhrase()));

        refreshTokenService.addRefreshToken(mobiUser, refreshToken);
    }

    private void addRefreshTokenToResponse(HttpServletResponse response, String refreshToken) {
        Cookie refreshCookie = cookieMethods.createCookie(REFRESH.getType(), refreshToken);
        refreshCookie.setSecure(false); // 개발 환경에서 HTTPS가 아니라면 false로 설정
        refreshCookie.setHttpOnly(true); // 클라이언트에서 쿠키 접근 방지
        refreshCookie.setPath("/"); // 도메인 전체에서 접근 가능하도록 설정
        refreshCookie.setMaxAge(60 * 60 * 24 * 7); // 7일 설정
        response.addCookie(refreshCookie);
    }

    private void redirectToTargetWithToken(HttpServletRequest request, HttpServletResponse response, String accessToken)
            throws IOException {
        response.setStatus(HttpStatus.OK.value());
        String targetUrl = getTargetUrl(accessToken);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    // 액세스 토큰을 queryParam에 추가
    private String getTargetUrl(String accessToken) {
        return UriComponentsBuilder.fromUriString("/")
                .queryParam("access", accessToken)
                .build()
                .toUriString();
    }
}