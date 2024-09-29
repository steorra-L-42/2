package com.example.mobipay.domain.registeredcard.entity.domain;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
public class RegisteredCardId implements Serializable {

    private String mobiUserId;
    private String ownedCardId;

}
