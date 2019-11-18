package com.gateway.api.controller;

import com.gateway.api.integration.order.UpdateStateOrderApi;
import com.gateway.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/updateOrderState")
    public void updateOrderState(@RequestBody UpdateStateOrderApi updateOrderStateApi) {
        orderService.updateOrderState(updateOrderStateApi);
    }

    @GetMapping(value = "orderState/order/{id}")
    public ResponseEntity<String> getOrderState(@PathVariable("id") Long id) {
        return ResponseEntity.ok("{\"state\":\"" + orderService.getOrderState(id) +"\"}");
    }

}