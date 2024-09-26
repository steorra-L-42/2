package com.example.merchant.global.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Constraint(validatedBy = RequestTimeValidator.class)
@Target({ElementType.FIELD})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface ValidRequestTime {

    String message() default "Entry time must be within 1 minute of the current time";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
