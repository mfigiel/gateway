package com.gateway.api.controller;

import com.gateway.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TransactionController {

    private final ClientService clientService;

    @GetMapping(value = "/transaction/{id}")
    public Object getOrderState(@PathVariable("id") Long id) {
        return clientService.getAllCustomerTransactions(id);
    }

    @GetMapping(value = "/transaction/{id}/{dateFrom}/{dateTo}")
    public List<Object> getOrderState(@PathVariable("id") long id, @PathVariable("dateFrom") String dateFrom, @PathVariable("dateTo") String dateTo) {
        return clientService.getAllClientTransactionWithDatePeriod(id, dateFrom, dateTo);
    }
}
