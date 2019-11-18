package com.gateway.containertests.tests;

import com.gateway.GatewayApplication;
import com.gateway.api.integration.ClientServiceClient;
import com.gateway.api.integration.order.OrderServiceClient;
import com.gateway.api.integration.warehouse.WarehouseClient;
import com.gateway.containertests.configuration.DockerEnvironmentConfiguration;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.annotation.PostConstruct;

import java.time.Duration;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {GatewayApplication.class})
public abstract class AbstractIntegrationContainerTest extends DockerEnvironmentConfiguration {

    @LocalServerPort
    protected int APPLICATION_PORT;
    @Autowired
    WarehouseClient warehouseClient;
    @Autowired
    ClientServiceClient serviceClient;
    @Autowired
    OrderServiceClient orderServiceClient;

    protected WebTestClient webTestClient;

    @BeforeAll
    static void abstractBeforeAll() {
        boolean useLocalEnvironment = isLocal();
        boolean useExistingEnv = useExistingEnv();
        boolean runWithoutContainers = DockerEnvironmentConfiguration.runWithoutContainers();

        if (useLocalEnvironment && !useExistingEnv && !runWithoutContainers) {
            if(localDockerEnvironment != null) {
                localDockerEnvironment.start();
            }
        }
        initializeApplicationContext(useLocalEnvironment, runWithoutContainers);
    }

    @PostConstruct
    private void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + APPLICATION_PORT)
                .responseTimeout(Duration.ofSeconds(90)).build();

        warehouseClient.setWarehouseAddress("http://localhost:" + WAREHOUSE_PORT);
        serviceClient.setClientServiceAddress("http://localhost:" + CLIENTS_PORT);
        orderServiceClient.setOrderServiceAddress("http://localhost:" + ORDERS_PORT);
    }

    private static void initializeApplicationContext(boolean useLocalConfiguration, boolean runWithoutContainers) {
        DockerEnvironmentConfiguration.local();

        ImmutableMap.Builder<String, String> propertiesBuilder = new ImmutableMap.Builder<String, String>()
                .put("eureka.client.enabled", "false")
                .put("PORT", "0");

        propertiesBuilder
                .build().forEach(System::setProperty);
    }
}
