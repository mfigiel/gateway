package com.gateway.api.validation;

import com.gateway.api.resource.AddressApi;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AddressValidator implements ConstraintValidator<AddressValidation, AddressApi> {

    @Override
    public boolean isValid(AddressApi addressApi, ConstraintValidatorContext constraintValidatorContext) {
        AddressApi address = addressApi;
        return isNotNullAndNotEmpty(address.getCity()) && isNotNullAndNotEmpty(address.getStreet()) &&
                isNotNullAndNotEmpty(address.getZipCode());
    }

    private boolean isNotNullAndNotEmpty(String field) {
        return field != null && !field.isEmpty();
    }

    void buildConstraint(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
