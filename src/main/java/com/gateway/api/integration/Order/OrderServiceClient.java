package com.gateway.api.integration.Order;

import com.gateway.api.mapping.OrderApiOrderMapperImpl;
import com.gateway.api.resource.OrderApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class OrderServiceClient {

    private static final String SINGLE_ORDER_ENDPOINT = "/order/";
    private static final String ALL_ORDERS_ENDPOINT = "/orders";
    private static final String ADD_ORDER_ENDPOINT = "/orders";
    private static final String UPDATE_ORDER_STATE_ENDPOINT = "/updateStateOrder";
    private static final String GET_ORDER_STATE_ENDPOINT = "/orderState/order/";
    private String orderServiceAddress = "http://orders";

    @Autowired
    private RestTemplate loadBalancedRestTemplate;

    public OrderApi getOrder(Long id){
        return loadBalancedRestTemplate.getForObject(orderServiceAddress + SINGLE_ORDER_ENDPOINT + id, OrderApi.class);
    }

    public List<OrderApi> getOrders(){
        return loadBalancedRestTemplate.exchange(orderServiceAddress + ALL_ORDERS_ENDPOINT,
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<OrderApi>>() {
                        }).getBody();
    }

    public Long addOrder(OrderApi newOrder){
        return loadBalancedRestTemplate.postForObject(orderServiceAddress + ADD_ORDER_ENDPOINT
                , new HttpEntity<>(prepareOrderRequest(newOrder)), OrderDto.class).getId();

    }

    private OrderDto prepareOrderRequest(OrderApi order) {
        OrderDto orderDto = new OrderApiOrderMapperImpl().orderApiToOrderDto(order);
        order.getProducts()
                .stream()
                .forEach(e -> orderDto.getProducts().add(e.getId().toString()));
        return orderDto;
    }

    public String getOrderState(Long id){
        return loadBalancedRestTemplate.getForObject(orderServiceAddress + GET_ORDER_STATE_ENDPOINT + id, String.class);
    }

    public void updateStateOrder(UpdateStateOrderApi updateOrderStateApi){
        loadBalancedRestTemplate.postForObject(orderServiceAddress + UPDATE_ORDER_STATE_ENDPOINT
                , new HttpEntity<>(updateOrderStateApi), Void.class);

    }

    public void setOrderServiceAddress(String orderServiceAddress) {
        this.orderServiceAddress = orderServiceAddress;
    }
}
