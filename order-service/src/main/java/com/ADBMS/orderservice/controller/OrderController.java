package com.ADBMS.orderservice.controller;

import com.ADBMS.orderservice.dto.OrderRequestDTO;
import com.ADBMS.orderservice.model.Order;
import com.ADBMS.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public List<Order> getAllOrders(){
        return orderService.getAllOrders();
    }

    @GetMapping("/{orderID}")
    public Order getOrderByOrderID(@PathVariable(name = "orderID") Long orderID) {
        return orderService.getOrderByOrderID(orderID);
    }

    @PostMapping
    public Order placeOrder(@RequestBody OrderRequestDTO orderRequest){
        return orderService.placeOrder(orderRequest);
    }

    @PutMapping("/{userId}/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable String userId, @PathVariable Long orderId){
        return orderService.cancelOrder(orderId);
    }
}
