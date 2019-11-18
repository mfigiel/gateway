package com.gateway.service;

import com.gateway.api.integration.order.OrderServiceClient;
import com.gateway.api.integration.order.UpdateStateOrderApi;
import com.gateway.api.resource.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderServiceClient orderClient;

    public void addOrderToTransaction(Transaction transaction) {
        transaction.getOrder().setClientId(transaction.getClient().getId());
        transaction.getOrder().setId(orderClient.addOrder(transaction.getOrder()));
    }

    public void updateOrderState(UpdateStateOrderApi updateOrderStateApi) {
        orderClient.updateStateOrder(updateOrderStateApi);
    }

    public String getOrderState(Long id) {
        return orderClient.getOrderState(id);
    }
}
