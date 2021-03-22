package com.gateway.api.integration.Order;

import lombok.Data;

@Data
public class UpdateStateOrderApi {

    Long orderId;
    String orderState;
    String paymentConfirmationNumber;
}
