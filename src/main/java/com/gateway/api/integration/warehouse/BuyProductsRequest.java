package com.gateway.api.integration.warehouse;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class BuyProductsRequest {
    List<Long> productsId;
}
