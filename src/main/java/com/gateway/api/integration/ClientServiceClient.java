package com.gateway.api.integration;

import com.gateway.api.resource.ClientApi;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
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

    @Retryable(value = {Exception.class}, maxAttempts = 6, backoff = @Backoff(delay = 1000))
    public ClientApi getClient(Long id) {
        return loadBalancedRestTemplate.getForObject(clientServiceAddress + SINGLE_CLIENT_ENDPOINT + id, ClientApi.class);
    }

    @Retryable(value = {Exception.class}, maxAttempts = 6, backoff = @Backoff(delay = 1000))
    public List<ClientApi> getClients() {
        return loadBalancedRestTemplate.exchange(clientServiceAddress + ALL_CLIENTS_ENDPOINT,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<ClientApi>>() {
                }).getBody();
    }

    @Retryable(value = {Exception.class}, maxAttempts = 6, backoff = @Backoff(delay = 1000))
    public ClientApi addClient(ClientApi newClient) {
        return loadBalancedRestTemplate.postForObject(clientServiceAddress + ADD_CLIENT_ENDPOINT
                , new HttpEntity<>(newClient), ClientApi.class);
    }


    public void setClientServiceAddress(String clientServiceAddress) {
        this.clientServiceAddress = clientServiceAddress;
    }

    @Retryable(value = {Exception.class}, maxAttempts = 6, backoff = @Backoff(delay = 1000))
    public List<Object> getAllClientTransactionWithDatePeriod(Long id, String dateFrom, String dateTo) {
        return loadBalancedRestTemplate.exchange(clientServiceAddress + GET_CLIENT_TRANSACTIONS_TIME_PERIOD + id + "/" + dateFrom + "/" + dateTo,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Object>>() {
                }).getBody();
    }

    @Retryable(value = {Exception.class}, maxAttempts = 6, backoff = @Backoff(delay = 1000))
    public Object getAllCustomerTransactions(Long id) {
        return loadBalancedRestTemplate.getForObject(clientServiceAddress + GET_CLIENT_TRANSACTION + id, Object.class);
    }
}
