package com.example.mobipay.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final int UNAUTHORIZED_STATUS = HttpServletResponse.SC_UNAUTHORIZED;
    private static final String CONTENT_TYPE = "application/json";
    private static final String CHARACTER_ENCODING = "UTF-8";
    private static final String JSON_MESSAGE_TEMPLATE = "{\"message\": \"%s\"}";
    private static final String UNAUTHORIZED_MESSAGE = "CustomOAuth2User가 null이거나 인증되지 않았습니다.";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 상태 코드를 설정
        response.setStatus(UNAUTHORIZED_STATUS);

        // 응답 본문에 JSON 형식으로 메시지 추가
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(CHARACTER_ENCODING);

        // 커스텀 메시지를 JSON 형식으로 작성
        String jsonResponse = String.format(JSON_MESSAGE_TEMPLATE, UNAUTHORIZED_MESSAGE);

        // 응답에 JSON 데이터 작성
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();

        log.info("★Unauthorized User accessed★");
    }
}
