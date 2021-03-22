package com.gateway.containertests.tests;

import com.gateway.api.integration.Order.UpdateStateOrderApi;
import com.gateway.api.integration.Warehouse.BuyProductsRequest;
import com.gateway.api.resource.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureWebTestClient(timeout = "90000")
public class GatewayContainerTest extends AbstractIntegrationContainerTest {

    public static final String SUBMITTED_STATE = "SUBMITTED";
    public static final String FULL_FILL_STATE = "fullFill";

    @Test
    void getProductsFromEmptyDatabase() {

        webTestClient.get()
                .uri("/product/178")
                .exchange()
                .expectStatus()
                .is5xxServerError();

        webTestClient.post()
                .uri("/buyProduct")
                .body(Mono.just(BuyProductsRequest.builder().productsId(new ArrayList<>(Arrays.asList(178L))).build()), BuyProductsRequest.class)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .json("");
    }

    @Test
    void addProductAndBuy() {
        ProductApi productApi = getSampleProduct();
        addProductToWarehouse(productApi);

        getAllProductsFromDatabase(productApi.getDescription());

        buyProduct();

        getAllProductsFromDatabaseAfterBuyOperation(productApi);
    }

    @Test
    void addProductAndBuyAndCheckOrderState() {
        ProductApi productApi = getSampleProduct();
        addProductToWarehouse(productApi);

        getAllProductsFromDatabase(productApi.getDescription());

        buyProduct();

        getAllProductsFromDatabaseAfterBuyOperation(productApi);

        getOrderState(1L, SUBMITTED_STATE);
        updateState(1L, FULL_FILL_STATE, null);
        getOrderState(1L, FULL_FILL_STATE);
    }

    private void getOrderState(Long orderId, String exceptedState) {
        webTestClient.get()
                .uri("/orderState/order/" + orderId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .consumeWith(response -> {
                    Object o = response.getResponseBody();
                    assertThat(o).isNotNull();
                });
    }

    private void getAllProductsFromDatabaseAfterBuyOperation(ProductApi product) {
        webTestClient.get()
                .uri("/products")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(new ParameterizedTypeReference<List<ProductApi>>() {})
                .value(productList ->{
                    List<ProductApi> filteredProductList = productList.stream()
                        .filter(productApi -> productApi.getDescription().equals(product.getDescription()))
                        .collect(Collectors.toList());
                    assertThat(filteredProductList.size()).isEqualTo(1);
                    assertThat(filteredProductList.get(0).getDescription()).isEqualTo(product.getDescription());
                        });
    }

    private void buyProduct() {
        webTestClient.post()
                .uri("/buyProduct")
                .body(Mono.just(createSampleTransaction()), Transaction.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Transaction.class)
                .getResponseBody()
                .doOnNext(transactionResult -> assertThat(verifyTransaction(transactionResult)).isTrue())
                .timeout(Duration.ofMillis(90000));
    }

    private void updateState(Long orderId, String orderState, String paymentConfirmationNumber) {
        webTestClient.post()
                .uri("/updateOrderState")
                .body(Mono.just(createSampleUpdateOrderStateObject(orderId, orderState, paymentConfirmationNumber)), UpdateStateOrderApi.class)
                .exchange()
                .expectStatus()
                .isOk();
    }


    private boolean verifyTransaction(Transaction transaction) {
        final OrderApi order = transaction.getOrder();
        Assertions.assertAll(() -> Optional
                .ofNullable(order.getProducts().stream().findFirst())
                .orElseThrow(RuntimeException::new));

        final ProductApi productApi = order.getProducts().stream().findFirst().get();
        if (order.getId() == 1 && transaction.getClient().getId() == 1
                && productApi.getUnitsInOrder() == 1
                && productApi.getUnitsInStock() == 9
                && productApi.getState().equals(ProductState.BOUGHT)) {
            return true;
        }

        return false;
    }

    private void getAllProductsFromDatabase(String description) {
        webTestClient.get()
                .uri("/products")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(new ParameterizedTypeReference<List<ProductApi>>() {
                })
                .value(productList -> assertThat(productList.stream().anyMatch(productApi -> productApi.getDescription().equals(description))).isTrue());
    }

    private void addProductToWarehouse(ProductApi product) {
        webTestClient.post()
                .uri("/products")
                .body(Mono.just(product), ProductApi.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json("");
    }

    @NotNull
    private Transaction createSampleTransaction() {
        Transaction transaction = new Transaction();
        OrderApi orderApi = new OrderApi();
        List<ProductApi> productApiList = new ArrayList<>();

        productApiList.add(ProductApi.builder().id(1L).build());
        orderApi.setProducts(productApiList);
        transaction.setOrder(orderApi);
        transaction.setClient(getSampleClientApi());
        return transaction;
    }

    @NotNull
    private UpdateStateOrderApi createSampleUpdateOrderStateObject( Long orderId, String newState, String paymentConfirmationNumber) {
        UpdateStateOrderApi updateStateOrderApi = new UpdateStateOrderApi();
        updateStateOrderApi.setOrderState(newState);
        updateStateOrderApi.setOrderId(orderId);
        updateStateOrderApi.setPaymentConfirmationNumber(paymentConfirmationNumber);

        return updateStateOrderApi;
    }

    @NotNull
    private ClientApi getSampleClientApi() {
        ClientApi clientApi = new ClientApi();
        clientApi.setName("testoweImie");
        clientApi.setSurname("testoweNazwisko");
        clientApi.setAddress(getSampleAddressApi());
        return clientApi;
    }

    @NotNull
    private AddressApi getSampleAddressApi() {
        AddressApi addressApi = new AddressApi();
        addressApi.setStreet("testowaUlica");
        addressApi.setZipCode("44-100");
        addressApi.setCity("testoweMIasto");
        addressApi.setHouseNumber(5);
        return addressApi;
    }

    private ProductApi getSampleProduct() {
        UUID uuid = UUID.randomUUID();
        return ProductApi.builder()
                .name("name")
                .category("category")
                .description(uuid.toString())
                .unitPrice(new BigDecimal(10))
                .unitsInStock(10L)
                .unitsInOrder(0L)
                .build();
    }

}
