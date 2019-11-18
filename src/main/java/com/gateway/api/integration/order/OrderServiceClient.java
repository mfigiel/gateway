package com.gateway.api.integration.order;

import com.gateway.api.mapping.OrderApiOrderMapperImpl;
import com.gateway.api.resource.OrderApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderServiceClient {

    private static final String SINGLE_ORDER_ENDPOINT = "/order/";
    private static final String ALL_ORDERS_ENDPOINT = "/orders";
    private static final String ADD_ORDER_ENDPOINT = "/orders";
    private static final String UPDATE_ORDER_STATE_ENDPOINT = "/updateStateOrder";
    private static final String GET_ORDER_STATE_ENDPOINT = "/orderState/order/";
    private String orderServiceAddress = "http://orders";
    private final RestTemplate loadBalancedRestTemplate;

    @Retryable(value = {Exception.class}, maxAttempts = 6, backoff = @Backoff(delay = 1000))
    public OrderApi getOrder(Long id){
        return loadBalancedRestTemplate.getForObject(orderServiceAddress + SINGLE_ORDER_ENDPOINT + id, OrderApi.class);
    }

    @Retryable(value = {Exception.class}, maxAttempts = 6, backoff = @Backoff(delay = 1000))
    public List<OrderApi> getOrders(){
        return loadBalancedRestTemplate.exchange(orderServiceAddress + ALL_ORDERS_ENDPOINT,
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<OrderApi>>() {
                        }).getBody();
    }

    @Retryable(value = {Exception.class}, maxAttempts = 6, backoff = @Backoff(delay = 1000))
    public Long addOrder(OrderApi newOrder){
        try {
            return loadBalancedRestTemplate.postForObject(orderServiceAddress + ADD_ORDER_ENDPOINT
                    , new HttpEntity<>(prepareOrderRequest(newOrder)), OrderDto.class).getId();
        } catch (NullPointerException e) {
            log.error("could not get id of new order" + e);
        }

        return null;
    }

    @Retryable(value = {Exception.class}, maxAttempts = 6, backoff = @Backoff(delay = 1000))
    public String getOrderState(Long id){
        return loadBalancedRestTemplate.getForObject(orderServiceAddress + GET_ORDER_STATE_ENDPOINT + id, String.class);
    }

    @Retryable(value = {Exception.class}, maxAttempts = 6, backoff = @Backoff(delay = 1000))
    public void updateStateOrder(UpdateStateOrderApi updateOrderStateApi){
        loadBalancedRestTemplate.postForObject(orderServiceAddress + UPDATE_ORDER_STATE_ENDPOINT
                , new HttpEntity<>(updateOrderStateApi), Void.class);

    }

    public void setOrderServiceAddress(String orderServiceAddress) {
        this.orderServiceAddress = orderServiceAddress;
    }


    private OrderDto prepareOrderRequest(OrderApi order) {
        OrderDto orderDto = new OrderApiOrderMapperImpl().orderApiToOrderDto(order);
        order.getProducts()
                .stream()
                .forEach(e -> orderDto.getProducts().add(e.getId().toString()));
        return orderDto;
    }
}
