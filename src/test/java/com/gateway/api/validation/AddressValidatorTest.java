package com.gateway.api.validation;

import com.gateway.api.resource.AddressApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.validation.ConstraintValidatorContext;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(JUnitPlatform.class)
class AddressValidatorTest {

    private ConstraintValidatorContext constraintValidatorContext;

    private AddressValidator validator;

    @BeforeEach
    void init() {
        constraintValidatorContext = Mockito.mock(ConstraintValidatorContext.class);
        validator = Mockito.spy(AddressValidator.class);
        doNothing().when(validator).buildConstraint(any(), anyString());
    }

    @Test
    void testOK_correctData() {
        // for
        AddressApi addressApi = new AddressApi();
        addressApi.setCity("City");
        addressApi.setStreet("Street");
        addressApi.setZipCode("44-100");

        // when
        boolean result = validator.isValid(addressApi, constraintValidatorContext);

        // then
        assertThat(result,is(true));
    }

    @Test
    void testFalse_cityNull() {
        // for
        AddressApi addressApi = new AddressApi();
        addressApi.setStreet("Street");
        addressApi.setZipCode("44-100");

        // when
        boolean result = validator.isValid(addressApi, constraintValidatorContext);

        // then
        assertThat(result,is(false));
    }

    @Test
    void testFalse_allIsEmpty() {
        // for
        AddressApi addressApi = new AddressApi();
        addressApi.setCity("");
        addressApi.setStreet("");
        addressApi.setZipCode("");

        // when
        boolean result = validator.isValid(addressApi, constraintValidatorContext);

        // then
        assertThat(result,is(false));
    }

    @Test
    void testFalse_allIsNull() {
        // for
        AddressApi addressApi = new AddressApi();

        // when
        boolean result = validator.isValid(addressApi, constraintValidatorContext);

        // then
        assertThat(result,is(false));
    }
}