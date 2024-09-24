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

//    @JsonProperty("token_type")
//    public String tokenType;
//
//    @JsonProperty("access_token")
//    public String accessToken;
//
//    @JsonProperty("id_token")
//    public String idToken;
//
//    @JsonProperty("access_token_expires_at")
//    public Date accessTokenExpiresAt;
//
//    @JsonProperty("refresh_token")
//    public String refreshToken;
//
//    @JsonProperty("refresh_token_expires_at")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "EEE MMM dd HH:mm:ss zzz yyyy")
//    public Date refreshTokenExpiresAt;
//
//    @JsonProperty("scope")
//    public String scope;
}