package com.gateway.api.integration.order;

import lombok.Data;

@Data
public class UpdateStateOrderApi {

    Long orderId;
    String orderState;
    String paymentConfirmationNumber;
}
