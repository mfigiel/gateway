package com.gateway.service;

import com.gateway.api.resource.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableBinding(TransactionSource.class)
public class TransactionService {

    private final WarehouseService warehouseService;
    private final OrderService orderService;
    private final ClientService clientService;
    private final TransactionSource transactionSource;

    public Transaction finishShopTransaction(Transaction transaction) {
        warehouseService.addProductsToTransaction(transaction, warehouseService.buyProducts(transaction));
        if (warehouseService.checkIfAllProductsWasBought(transaction)) {
            clientService.addClientToTransaction(transaction);
            orderService.addOrderToTransaction(transaction);
            setTransactionStatusToFinish(transaction);
        }
        sendTransactionToClientService(transaction);
        return transaction;
    }

    private void sendTransactionToClientService(Transaction transaction) {
        transactionSource.sendTransaction().send(MessageBuilder.withPayload(transaction).build());
    }

    private void setTransactionStatusToFinish(Transaction transaction) {
        transaction.setFinished(true);
    }
}
