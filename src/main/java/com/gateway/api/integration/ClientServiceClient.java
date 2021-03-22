package com.gateway.api.integration;

import com.gateway.api.resource.ClientApi;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClientServiceClient {

    private static final String SINGLE_CLIENT_ENDPOINT = "/client/";
    private static final String ALL_CLIENTS_ENDPOINT = "/clients";
    private static final String ADD_CLIENT_ENDPOINT = "/clients";
    private static final String GET_CLIENT_TRANSACTION = "/transaction/";
    private static final String GET_CLIENT_TRANSACTIONS_TIME_PERIOD = "/transaction/";
    private String clientServiceAddress = "http://clients";

    private final RestTemplate loadBalancedRestTemplate;

    public ClientApi getClient(Long id) {
        return loadBalancedRestTemplate.getForObject(clientServiceAddress + SINGLE_CLIENT_ENDPOINT + id, ClientApi.class);
    }

    public List<ClientApi> getClients() {
        return loadBalancedRestTemplate.exchange(clientServiceAddress + ALL_CLIENTS_ENDPOINT,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<ClientApi>>() {
                }).getBody();
    }

    public ClientApi addClient(ClientApi newClient) {
        return loadBalancedRestTemplate.postForObject(clientServiceAddress + ADD_CLIENT_ENDPOINT
                , new HttpEntity<>(newClient), ClientApi.class);
    }


    public void setClientServiceAddress(String clientServiceAddress) {
        this.clientServiceAddress = clientServiceAddress;
    }

    public List<Object> getAllClientTransactionWithDatePeriod(Long id, String dateFrom, String dateTo) {
        return loadBalancedRestTemplate.exchange(clientServiceAddress + GET_CLIENT_TRANSACTIONS_TIME_PERIOD + id + "/" + dateFrom + "/" + dateTo,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Object>>() {
                }).getBody();
    }

    public Object getAllCustomerTransactions(Long id) {
        return loadBalancedRestTemplate.getForObject(clientServiceAddress + GET_CLIENT_TRANSACTION + id, Object.class);
    }
}
