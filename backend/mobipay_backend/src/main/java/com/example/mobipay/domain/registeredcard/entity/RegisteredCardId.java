package com.example.mobipay.domain.registeredcard.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RegisteredCardId implements Serializable {

    private Long mobiUserId;
    private Long ownedCardId;

    public static RegisteredCardId of(Long mobiUserId, Long ownedCardId) {
        return new RegisteredCardId(mobiUserId, ownedCardId);
    }
}
