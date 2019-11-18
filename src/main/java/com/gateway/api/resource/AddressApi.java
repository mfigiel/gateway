package com.gateway.api.resource;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddressApi {

    @NotEmpty
    @Size(min=2, max=40)
    private String city;
    @NotEmpty
    @Size(min=2, max=40)
    private String street;
    private Integer houseNumber;
    private Integer flatNumber;
    @NotEmpty
    private String zipCode;

}
