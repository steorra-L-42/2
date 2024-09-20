package com.example.mobipay.oauth2.util;

import com.example.mobipay.oauth2.dto.OAuth2Response;

public class AuthIdCreator {
    public static String getAuthId(OAuth2Response response) {
        return response.getProvider() + " " + response.getProviderId();
    }
}
