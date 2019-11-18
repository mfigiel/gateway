package com.gateway.api.integration.order;

import lombok.Data;

@Data
public class UpdateStateOrderApi {

    Long orderId;
    OrderEvents orderEvents;
    String paymentConfirmationNumber;
}
