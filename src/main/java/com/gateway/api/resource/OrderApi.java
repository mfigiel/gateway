package com.gateway.api.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderApi {

    private Long id;

    @NotEmpty
    private List<ProductApi> products;

    @NotEmpty
    private Long clientId;

    private Date orderDate = new Date();
}
