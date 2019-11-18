package com.gateway.api.resource;

import com.gateway.api.validation.AddressValidation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientApi {

    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String surname;
    @AddressValidation
    private AddressApi address;

}
