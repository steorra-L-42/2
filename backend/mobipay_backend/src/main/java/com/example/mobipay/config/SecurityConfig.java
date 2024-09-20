package com.example.mobipay.config;

import com.example.mobipay.handler.CustomSuccessHandler;
import com.example.mobipay.oauth2.jwt.JWTFilter;
import com.example.mobipay.oauth2.jwt.JWTUtil;
import com.example.mobipay.oauth2.jwt.LoginFilter;
import com.example.mobipay.oauth2.service.CustomOauth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomOauth2UserService customOauth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration,
                          CustomOauth2UserService customOauth2UserService, CustomSuccessHandler customSuccessHandler,
                          JWTUtil jwtUtil) {

        this.authenticationConfiguration = authenticationConfiguration;
        this.customOauth2UserService = customOauth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

//                        configuration.setAllowedOrigins(Collections.singletonList("프론트 주소"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                        return configuration;
                    }
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

        //필터 추가 LoginFilter()는 인자를 받음 (AuthenticationManager() 메소드에 authenticationConfiguration 객체를 넣어야 함) 따라서 등록 필요
//        http
//                .authorizeHttpRequests((auth) -> auth
//                        .requestMatchers("/login", "/", "/join").permitAll()
//                        .anyRequest().authenticated());

        //JWTFilter 추가
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration)),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        //oauth2
        http
                .oauth2Login((oauth2) -> oauth2
                        .authorizationEndpoint(authorizationEndpointConfig ->
                                authorizationEndpointConfig.baseUri("/api/v1/oauth2/authorization"))
                        .userInfoEndpoint((userInfoEndpointConfig ->
                                userInfoEndpointConfig.userService(customOauth2UserService)))
                        .successHandler(customSuccessHandler)
                );

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/login", "/oauth2/authorization/kakao")
                        .permitAll()  // 루트, 로그인, 카카오 인증 경로는 허용
                        .anyRequest().authenticated());

        //세션 설정 : STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
