package com.gateway.api.controller;

import com.gateway.api.resource.ProductApi;
import com.gateway.api.resource.Transaction;
import com.gateway.service.TransactionService;
import com.gateway.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ProductController {

    private final WarehouseService warehouseService;
    private final TransactionService transactionService;

    @GetMapping("/products")
    public List<ProductApi> getProducts() {
        return warehouseService.getProducts();
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public void addProduct(@RequestBody ProductApi product) {
        warehouseService.addProduct(product);
    }

    @GetMapping(value = "/product/{id}")
    public ProductApi getProductInformation(@PathVariable("id") long id) {
        return warehouseService.getProduct(id);
    }

    @PostMapping(value = "/buyProduct")
    public Transaction buyProduct(@Valid @RequestBody Transaction transaction) {
       return transactionService.finishShopTransaction(transaction);
    }

}
