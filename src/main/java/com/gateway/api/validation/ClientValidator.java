package com.gateway.api.validation;

import com.gateway.api.resource.ClientApi;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ClientValidator implements ConstraintValidator<ClientValidation, ClientApi> {

    @Override
    public boolean isValid(ClientApi client, ConstraintValidatorContext constraintValidatorContext) {
        return client != null && client.getAddress()!= null && client.getName()!= null
                && client.getSurname() != null;
    }
}
