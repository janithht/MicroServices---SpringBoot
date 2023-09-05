package com.ADBMS.orderservice.controller;

import com.ADBMS.orderservice.dto.OrderRequestDTO;
import com.ADBMS.orderservice.model.Order;
import com.ADBMS.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order placeOrder(@RequestBody OrderRequestDTO orderRequest){
        return orderService.placeOrder(orderRequest);
    }

}
