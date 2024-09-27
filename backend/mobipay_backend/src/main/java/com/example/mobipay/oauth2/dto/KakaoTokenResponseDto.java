package com.example.mobipay.oauth2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import java.util.List;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoTokenResponseDto {

    private String accessToken;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "EEE MMM dd HH:mm:ss zzz yyyy")
    private Date accessTokenExpiresIn;

    private String refreshToken;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "EEE MMM dd HH:mm:ss zzz yyyy")
    private Date refreshTokenExpiresIn;

    private String idToken;

    private List<String> scopes;


}