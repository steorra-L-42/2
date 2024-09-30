package com.example.merchant.util.pos.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SessionId {

    private String sessionId;

    public SessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
