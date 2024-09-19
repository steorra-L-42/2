package com.example.mobipay.oauth2.util;

import static com.example.mobipay.oauth2.enums.TokenType.REFRESH;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieMethods {

    public Cookie createCookie(String key, String value) {

        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie(key, value);
        cookie.setMaxAge(REFRESH.getExpiration());
//        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        return cookie;
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH.getType(), null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}
