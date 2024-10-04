package com.example.mobipay.config;

import com.example.mobipay.domain.fcmtoken.repository.FcmTokenRepository;
import com.example.mobipay.domain.mobiuser.repository.MobiUserRepository;
import com.example.mobipay.domain.refreshtoken.repository.RefreshTokenRepository;
import com.example.mobipay.oauth2.jwt.CustomLogoutFilter;
import com.example.mobipay.oauth2.jwt.JWTFilter;
import com.example.mobipay.oauth2.jwt.JWTUtil;
import com.example.mobipay.oauth2.service.UserService;
import com.example.mobipay.oauth2.util.CookieMethods;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationCodeGrantFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieMethods cookieMethods;
    private final FcmTokenRepository fcmTokenRepository;
    private final MobiUserRepository mobiUserRepository;
    private final UserService userService;

    String[] whitelist_post = {
            "/api/v1/user/reissue"
            // 논의 후 추가 필요
    };
    String[] whitelist_get = {
            "/",
            "/api/v1/user/login/**"
            // 논의 후 추가 필요
    };

    @Value("${cors.url}")
    private String corsURL;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String[] allowedOrigins = Arrays.stream(corsURL.split(","))
                .map(String::trim)
                .toArray(String[]::new);

        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {

                    CorsConfiguration configuration = new CorsConfiguration();

                    configuration.setAllowedOrigins(List.of(allowedOrigins)); //CORS 허용 도메인 설정
                    configuration.setAllowedMethods(Collections.singletonList("*")); //허용된 HTTP 메서드 설정
                    configuration.setAllowCredentials(true); //쿠키와 인증 정보 허용
                    configuration.setAllowedHeaders(Collections.singletonList("*")); //허용된 요청 헤더 설정
                    configuration.setMaxAge(3600L); //프리플라이트 요청 캐싱 시간
                    configuration.setExposedHeaders(List.of("Set-Cookie", "Authorization")); //클라이언트에게 노출할 응답 헤더

                    return configuration;
                }));

        //csrf disable
        http
                .csrf((auth) -> auth.disable());

        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //HTTP Basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        //JWTFilter 추가
        http
                .addFilterBefore(new JWTFilter(jwtUtil), OAuth2AuthorizationCodeGrantFilter.class);

        //oauth2
        http
                .oauth2Login((oauth2) -> oauth2
                        .authorizationEndpoint(authorizationEndpointConfig ->
                                authorizationEndpointConfig.baseUri("/api/v1/oauth2/authorization"))
                        .redirectionEndpoint(redirectionEndpointConfig ->
                                redirectionEndpointConfig.baseUri("/api/v1/login/oauth2/code/*"))
                );

        http
                .addFilterBefore(
                        new CustomLogoutFilter(jwtUtil, refreshTokenRepository, fcmTokenRepository, mobiUserRepository,
                                cookieMethods),
                        LogoutFilter.class);

        http
                .logout((oauth2) -> oauth2
                        .logoutUrl("/api/v1/users/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .anyRequest().permitAll());

        //세션 설정 : STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
