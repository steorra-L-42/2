package com.example.mobipay.domain.cargroup.entity;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
public class CarGroupId implements Serializable {

    private Long carId;
    private Long mobiUserId;
}
