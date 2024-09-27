package com.example.mobipay.global.authentication.dto.ssafyusercheck;

import com.example.mobipay.global.authentication.dto.SsafyUserResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SsafyUserCheckResponse implements SsafyUserResponse {

    @NotNull
    @Size(max = 40)
    private String userId;

    @NotNull
    @Size(max = 10)
    private String userName;

    @NotNull
    @Size(max = 40)
    private String institutionCode;

    @NotNull
    @Size(max = 60)
    private String userKey;

    @NotNull
    @Size(max = 10)
    private OffsetDateTime created;

    @NotNull
    @Size(max = 10)
    private OffsetDateTime modified;

    @NotNull
    private HttpStatus httpStatus;
}