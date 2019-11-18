package com.gateway.api.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AddressValidator.class)
@Target( {ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AddressValidation {
    // default error message
    String message() default "Person has invalid informations";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default {}; // required
}
