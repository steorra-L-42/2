package com.example.merchant.global.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class RequestTimeValidator implements ConstraintValidator<ValidRequestTime, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime entry, ConstraintValidatorContext context) {
        if (entry == null) {
            return false;
        }
        boolean betweenFuture = entry.isBefore(LocalDateTime.now().plusMinutes(1));
        boolean betweenPast =entry.isAfter(LocalDateTime.now().minusMinutes(1));
        return  betweenFuture && betweenPast;
    }
}
