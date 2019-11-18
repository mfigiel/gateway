package com.gateway.api.resource;

import com.gateway.api.validation.ClientValidation;
import com.gateway.api.validation.OrderValidation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @OrderValidation
    OrderApi order;

    @ClientValidation
    ClientApi client;
    boolean finished;
}
