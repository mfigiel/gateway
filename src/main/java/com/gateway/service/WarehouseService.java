package com.gateway.service;

import com.gateway.api.integration.warehouse.BuyProductsRequest;
import com.gateway.api.integration.warehouse.BuyProductsResponse;
import com.gateway.api.integration.warehouse.WarehouseClient;
import com.gateway.api.resource.ProductApi;
import com.gateway.api.resource.ProductState;
import com.gateway.api.resource.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseClient warehouseClient;

    public void addProductsToTransaction(Transaction transaction, BuyProductsResponse buyProductsResponse) {
        transaction.getOrder().setProducts(buyProductsResponse.getProducts());
    }

    public boolean checkIfAllProductsWasBought(Transaction transaction) {
        return transaction.getOrder().getProducts().stream().allMatch(product -> product.getState().equals(ProductState.BOUGHT));
    }

    public BuyProductsResponse buyProducts(Transaction transaction) {
        List<Long> productsId = transaction.getOrder().getProducts().stream().map(ProductApi::getId).collect(Collectors.toList());
        return warehouseClient.buyProduct(BuyProductsRequest.builder().productsId(productsId).build());
    }

    public ProductApi getProduct(long id) {
        return warehouseClient.getProduct(id);
    }

    public void addProduct(ProductApi product) {
        warehouseClient.addProduct(product);
    }

    public List<ProductApi> getProducts() {
        return warehouseClient.getProducts();
    }
}
