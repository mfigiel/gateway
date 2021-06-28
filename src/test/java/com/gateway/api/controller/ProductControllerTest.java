package com.gateway.api.controller;

import com.gateway.api.resource.ProductApi;
import com.gateway.service.WarehouseService;
import org.mockito.Mockito;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class ProductControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    WarehouseService warehouseService;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void productsGet() throws Exception {

        Mockito.when(warehouseService.getProducts()).thenReturn(new ArrayList<>());

        mockMvc
                .perform(get("/products"))
                .andExpect(status().isOk());

        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/products",
                String.class)).isNotNull();
    }

    @Test
    public void productGet() throws Exception {

        Mockito.when(warehouseService.getProduct(1)).thenReturn(new ProductApi());

        mockMvc
                .perform(get("/product", 1)
                .param("id", "1"))
                .andExpect(status().is4xxClientError());

        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/product/?id=1",
                String.class)).contains("\"status\":404,\"error\":\"Not Found\",\"message\":\"\",\"path\":\"/product/\"");
    }
}
