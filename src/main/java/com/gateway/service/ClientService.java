package com.gateway.service;

import com.gateway.api.integration.ClientServiceClient;
import com.gateway.api.resource.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientServiceClient clientService;

    public void addClientToTransaction(Transaction transaction) {
        transaction.setClient(clientService.addClient(transaction.getClient()));
    }

    public List<Object> getAllClientTransactionWithDatePeriod(Long id, String dateFrom, String dateTo) {
        return clientService.getAllClientTransactionWithDatePeriod(id, dateFrom, dateTo);
    }

    public Object getAllCustomerTransactions(Long id) {
        return clientService.getAllCustomerTransactions(id);
    }
}
