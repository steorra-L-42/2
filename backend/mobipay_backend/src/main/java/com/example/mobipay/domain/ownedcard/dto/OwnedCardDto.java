package com.example.mobipay.domain.ownedcard.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OwnedCardDto {
    private Long id;
    private String cardNo;
    private String cvc;
    private String withdrawalDate;
    private String cardExpiryDate;
    private LocalDateTime created;
}
