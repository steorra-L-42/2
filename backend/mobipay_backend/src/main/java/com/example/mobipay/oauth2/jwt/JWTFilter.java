package com.example.mobipay.oauth2.jwt;

import static com.example.mobipay.oauth2.enums.JWTFilterMessage.EXPIRED_ACCESS_TOKEN;
import static com.example.mobipay.oauth2.enums.JWTFilterMessage.NO_ACCESS_TOKEN;
import static com.example.mobipay.oauth2.enums.TokenType.ACCESS;
import static com.example.mobipay.oauth2.enums.TokenType.BEARER;

import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.example.mobipay.oauth2.dto.UserDTO;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

//        if (isExemptedUri(request)) {
//            doFilter(request, response, filterChain);
//            return;
//        }

        String header = request.getHeader(ACCESS.getType());
        if (header == null) {
            doFilter(request, response, filterChain);
            return;
        }

        if (invalidPrefix(response, header)) {
            return;
        }

        String accessToken = extractToken(header);
        if (isTokenInvalid(accessToken, response)) {
            return;
        }

        setUpAuthentication(accessToken);
        filterChain.doFilter(request, response);
    }

    private boolean invalidPrefix(HttpServletResponse response, String header) throws IOException {
        String prefix = extractPrefix(header);

//        접두사가 일ㅊ ㅣ한다면 false 반환
        if (prefix.equals((BEARER.getType()))) {
            return false;
        }
//        접두사가 일치하지 않는다면 예외처리
        setResponse(response, EXPIRED_ACCESS_TOKEN.toJson());
        return true;
    }

    private boolean isNotAccessToken(String accessToken, HttpServletResponse response) throws IOException {
        if (jwtUtil.getCategory(accessToken).equals(ACCESS.getType())) {
            return false;
        }
        // 엑세스토큰이 아닐 경우
        setResponse(response, NO_ACCESS_TOKEN.toJson());
        return true;
    }

    //  접두사 추출
    private String extractPrefix(String header) {
        return header.substring(0, BEARER.getType().length());
    }

    // 토큰 추출
    private String extractToken(String header) {
        return header.substring(BEARER.getType().length());
    }


    private void setResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        //헤더설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        //Responsebody 섲정
        PrintWriter writer = response.getWriter();
        writer.print(message);
        writer.flush();
    }

    private void setUpAuthentication(String accessToken) {
        String email = jwtUtil.getEmail(accessToken);
        String name = jwtUtil.getName(accessToken);
        String picture = jwtUtil.getPicture(accessToken);
        String phonenumber = jwtUtil.getPhoneNumber(accessToken);

        UserDTO userDTO = UserDTO.builder()
                .email(email)
                .name(name)
                .picture(picture)
                .phonenumber(phonenumber)
                .build();
        //CustomOAuth2User에 유저 정보 담기
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);
        // 스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null,
                customOAuth2User.getAuthorities());
        System.out.println("JWTFilter authToken : " + authToken);
        // SecurityContextHolder에 일시적인 세션 생성
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }


    private boolean isTokenInvalid(String accessToken, HttpServletResponse response) throws IOException {
        return isTokenExpired(accessToken, response) || isNotAccessToken(accessToken, response);
    }

    private boolean isTokenExpired(String accessToken, HttpServletResponse response) throws IOException {
        try {
            jwtUtil.isExpired(accessToken);
            // 정상적이라면 Exception이 발생하지 않음
            return false;
        } catch (ExpiredJwtException e) {
            // 만료된 Jwt 토큰이라면
            setResponse(response, EXPIRED_ACCESS_TOKEN.toJson());
            return true;
        }
    }
}