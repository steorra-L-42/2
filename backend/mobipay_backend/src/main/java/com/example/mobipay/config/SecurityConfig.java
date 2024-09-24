package com.example.mobipay.config;

import com.example.mobipay.oauth2.handler.CustomSuccessHandler;
import com.example.mobipay.oauth2.jwt.CustomLogoutFilter;
import com.example.mobipay.oauth2.jwt.JWTFilter;
import com.example.mobipay.oauth2.jwt.JWTUtil;
import com.example.mobipay.oauth2.repository.MobiUserRepository;
import com.example.mobipay.oauth2.repository.RefreshTokenRepository;
import com.example.mobipay.oauth2.service.CustomOauth2UserService;
import com.example.mobipay.oauth2.util.CookieMethods;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomOauth2UserService customOauth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieMethods cookieMethods;
    private final MobiUserRepository mobiUserRepository;

    String[] whitelist_post = {
            "/api/v1/users/reissue",
            "http://localhost:8080/api/v1/login",
            "/api/v1/login"
    };
    String[] whitelist_get = {
            "/",
            "/login",
            "/login/**",
//            "/api/v1/login/oauth2/code/kakao",
            "/login/oauth2/**",
            "/api/v1/login"
    };

//    @Value()
//    String corsURL;

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//
//        return configuration.getAuthenticationManager();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        String[] allowedOrigins = Arrays.stream(corsURL.split(","))
//                .map(String::trim)
//                .toArray(String[]::new);

        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {

                    CorsConfiguration configuration = new CorsConfiguration();

                    configuration.setAllowedOrigins(List.of(
                            "https://mobipay.kr/api/v1/**",
                            "http://localhost:8080",
                            "http://10.0.2.2:8080",   // 안드로이드 에뮬레이터
                            "http://172.18.96.1:8080" // 로컬 네트워크에서 접근하는 실제 기기 (IP는 본인의 IP로 변경)
                    ));
                    configuration.setAllowedMethods(Collections.singletonList("*"));
                    configuration.setAllowCredentials(true);
                    configuration.setAllowedHeaders(Collections.singletonList("*"));
                    configuration.setMaxAge(3600L);
                    configuration.setExposedHeaders(List.of("Set-Cookie", "Authorization"));

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
                        .userInfoEndpoint((userInfoEndpointConfig ->
                                userInfoEndpointConfig.userService(customOauth2UserService)))
                        .successHandler(customSuccessHandler)
                        .redirectionEndpoint(redirectionEndpointConfig ->
                                redirectionEndpointConfig.baseUri("/api/v1/login/oauth2/code/*"))
                );

        http
                .addFilterBefore(
                        new CustomLogoutFilter(jwtUtil, refreshTokenRepository, cookieMethods, mobiUserRepository),
                        LogoutFilter.class);

        http
                .logout((oauth2) -> oauth2
                        .logoutUrl("/api/v1/oauth2/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(HttpMethod.POST, whitelist_post).permitAll()
                        .requestMatchers(HttpMethod.GET, whitelist_get).permitAll()
                        .anyRequest().authenticated());

        //세션 설정 : STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
