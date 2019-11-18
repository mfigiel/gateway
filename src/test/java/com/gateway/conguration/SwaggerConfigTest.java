package com.gateway.conguration;

import com.gateway.configuration.SwaggerConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import springfox.documentation.spring.web.plugins.Docket;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(MockitoJUnitRunner.class)
public class SwaggerConfigTest {

    SwaggerConfig swaggerConfig = new SwaggerConfig();

    @Test
    public void method() {
        Docket docket = swaggerConfig.documentApi();

        assertThat("Invalid swagger config", docket, is(notNullValue()));
    }
}
