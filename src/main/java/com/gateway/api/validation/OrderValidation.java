package com.gateway.api.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = OrderValidator.class)
@Target( {ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface OrderValidation {
    // default error message
    String message() default "Order has invalid informations";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default {}; // required
}
