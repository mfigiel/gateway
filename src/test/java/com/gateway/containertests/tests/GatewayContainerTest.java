package com.gateway.containertests.tests;

import com.gateway.api.integration.order.OrderEvents;
import com.gateway.api.integration.order.UpdateStateOrderApi;
import com.gateway.api.integration.warehouse.BuyProductsRequest;
import com.gateway.api.resource.*;
import org.junit.jupiter.api.*;
import org.springframework.core.ParameterizedTypeReference;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class GatewayContainerTest extends AbstractIntegrationContainerTest {

    private static final String SUBMITTED_STATE = "SUBMITTED";
    private static final String FULL_FILL_STATE = "FULLFILLED";
    private static final String CANCEL_STATE = "CANCEL";
    public static final String PAID_STATE = "PAID";
    private static final long BASE_UNITS_IN_STOCK = 10L;
    private static final long BASE_UNITS_IN_ORDER = 0L;
    private static final int BASE_PRODUCT_PRICE = 10;


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

        getTransactions(1L);

        getAllProductsFromDatabaseAfterBuyOperation(productApi);
    }

    @Test
    void buyNotExistingProduct() {
        buyNoExistingProduct();
    }

    @Test
    void getTransactionByNotExistingClient() {
        webTestClient.get()
                .uri("/transaction/" + 0)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(new ParameterizedTypeReference<List<Object>>() {
                })
                .getResponseBody()
                .doOnNext(transactionList -> {
                    assertThat(transactionList.size()).isZero();
                });
    }

    @Test
    void addProductAndBuyAndCheckOrderState() {
        ProductApi productApi = getSampleProduct();
        addProductToWarehouse(productApi);

        getAllProductsFromDatabase(productApi.getDescription());

        buyProduct();

        getAllProductsFromDatabaseAfterBuyOperation(productApi);

        getOrderState(1L, SUBMITTED_STATE);
        updateState(1L, OrderEvents.PAY, "1234");
        getOrderState(1L, PAID_STATE);
        updateState(1L, OrderEvents.FULFILL, null);
        getOrderState(1L, FULL_FILL_STATE);
    }

    private void getOrderState(Long orderId, String expectedState) {
        webTestClient.get()
                .uri("/orderState/order/" + orderId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                .value(state -> assertThat(state).contains(expectedState));
    }


    private void getTransactions(Long clientId) {
        webTestClient.get()
                .uri("/transaction/" + clientId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(new ParameterizedTypeReference<List<Object>>() {
                })
                .value(transactionList -> assertThat(transactionList.size()).isPositive());
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
                    assertThat(filteredProductList.size()).isPositive();
                    assertThat(filteredProductList.stream().anyMatch(filteredProduct -> filteredProduct.getDescription().equals(product.getDescription())));
                        });
    }

    private void buyProduct() {
        webTestClient.post()
                .uri("/buyProduct")
                .body(Mono.just(createSampleTransaction(1L)), Transaction.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Transaction.class)
                .getResponseBody()
                .doOnNext(transactionResult -> assertThat(verifyTransaction(transactionResult)).isTrue())
                .subscribe();
    }

    private void buyNoExistingProduct() {
        webTestClient.post()
                .uri("/buyProduct")
                .body(Mono.just(createSampleTransaction(100L)), Transaction.class)
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }

    private void updateState(Long orderId, OrderEvents event, String paymentConfirmationNumber) {
        webTestClient.post()
                .uri("/updateOrderState")
                .body(Mono.just(createSampleUpdateOrderStateObject(orderId, event, paymentConfirmationNumber)), UpdateStateOrderApi.class)
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
                && productApi.getUnitsInOrder() == (BASE_UNITS_IN_ORDER + 1)
                && productApi.getUnitsInStock() == (BASE_UNITS_IN_STOCK - 1)
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
                .expectStatus()
                .isCreated()
                .expectBody()
                .json("");
    }

    private Transaction createSampleTransaction(Long productId) {
        Transaction transaction = new Transaction();
        OrderApi orderApi = new OrderApi();
        List<ProductApi> productApiList = List.of(ProductApi.builder().id(productId).build());

        orderApi.setProducts(productApiList);
        transaction.setOrder(orderApi);
        transaction.setClient(getSampleClientApi());
        return transaction;
    }

    private UpdateStateOrderApi createSampleUpdateOrderStateObject(Long orderId, OrderEvents event, String paymentConfirmationNumber) {
        UpdateStateOrderApi updateStateOrderApi = new UpdateStateOrderApi();
        updateStateOrderApi.setOrderEvents(event);
        updateStateOrderApi.setOrderId(orderId);
        updateStateOrderApi.setPaymentConfirmationNumber(paymentConfirmationNumber);

        return updateStateOrderApi;
    }

    private ClientApi getSampleClientApi() {
        ClientApi clientApi = new ClientApi();
        clientApi.setName("testoweImie");
        clientApi.setSurname("testoweNazwisko");
        clientApi.setAddress(getSampleAddressApi());
        return clientApi;
    }

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
                .unitPrice(new BigDecimal(BASE_PRODUCT_PRICE))
                .unitsInStock(BASE_UNITS_IN_STOCK)
                .unitsInOrder(BASE_UNITS_IN_ORDER)
                .build();
    }

}
