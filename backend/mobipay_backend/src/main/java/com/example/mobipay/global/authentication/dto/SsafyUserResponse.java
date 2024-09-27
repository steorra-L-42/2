package com.example.mobipay.global.authentication.dto;

import java.time.OffsetDateTime;

public interface SsafyUserResponse {

    String getUserId();

    String getUserName();

    String getUserKey();

    OffsetDateTime getCreated();

    OffsetDateTime getModified();
}
