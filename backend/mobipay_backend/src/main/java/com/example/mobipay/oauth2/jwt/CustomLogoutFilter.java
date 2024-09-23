package com.example.mobipay.oauth2.jwt;

import static com.example.mobipay.oauth2.enums.TokenType.REFRESH;

import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.oauth2.repository.MobiUserRepository;
import com.example.mobipay.oauth2.repository.RefreshTokenRepository;
import com.example.mobipay.oauth2.util.CookieMethods;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private static final Pattern LOGOUT_PATTERN = Pattern.compile("^/api/v1/users/logout$");
    private static final String POST_METHOD = "POST";

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieMethods cookieMethods;
    private final MobiUserRepository mobiUserRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        doFilterInternal((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        if (isNotLogoutRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = getRefreshTokenFromCookies(request.getCookies());

        if (refreshToken == null || isInvalidRefreshToken(refreshToken)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        processLogout(response, refreshToken);
    }

    private boolean isNotLogoutRequest(HttpServletRequest request) {
        return !(LOGOUT_PATTERN.matcher(request.getRequestURI()).matches() && POST_METHOD.equals(request.getMethod()));
    }

    // 쿠키가 하나도 없을 결우 null 반환
    private String getRefreshTokenFromCookies(Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(REFRESH.getType())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private boolean isInvalidRefreshToken(String refreshToken) {
        try {
            if (jwtUtil.isExpired(refreshToken) || !REFRESH.getType().equals(jwtUtil.getCategory(refreshToken))) {
                return true;
            }
        } catch (ExpiredJwtException e) {
            return true;
        }
        return !refreshTokenRepository.existsByValue(refreshToken);
    }

    @Transactional
    protected void processLogout(HttpServletResponse response, String refreshToken) {
        MobiUser mobiUser = findMobiUserByRefreshToken(refreshToken);

        clearMobiUserRefreshToken(mobiUser);
        refreshTokenRepository.revokeByValue(refreshToken);

        cookieMethods.clearRefreshTokenCookie(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Transactional
    protected void clearMobiUserRefreshToken(MobiUser mobiUser) {
        mobiUser.deleteRefreshToken();
        mobiUserRepository.save(mobiUser);
    }

    private MobiUser findMobiUserByRefreshToken(String refreshToken) {
        Long userId = jwtUtil.getUserId(refreshToken);
        Optional<MobiUser> optionalMobiUser = mobiUserRepository.findById(userId);

        if (optionalMobiUser.isEmpty()) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        return optionalMobiUser.get();
    }

}
