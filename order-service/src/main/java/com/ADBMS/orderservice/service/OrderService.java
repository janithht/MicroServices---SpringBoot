package com.ADBMS.orderservice.service;

import com.ADBMS.orderservice.dto.InventoryResponseDTO;
import com.ADBMS.orderservice.dto.OrderItemRequestDTO;
import com.ADBMS.orderservice.dto.OrderRequestDTO;
import com.ADBMS.orderservice.dto.UserDTO;
import com.ADBMS.orderservice.exception.*;
import com.ADBMS.orderservice.model.Order;
import com.ADBMS.orderservice.model.OrderItem;
import com.ADBMS.orderservice.repository.OrderItemRepository;
import com.ADBMS.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final WebClient.Builder webClientBuilder;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderByOrderID(Long orderID) {
        return orderRepository.findById(orderID).orElseThrow(
                () -> new OrderNotFoundException("Order is not found with orderID : " + orderID)
        );
    }

    @Transactional
    public Order placeOrder(OrderRequestDTO orderRequestDTO) {
        // 1. fetching user information from the user-microservice
        UserDTO userDTO = fetchUserInformation(orderRequestDTO.getUserID());

        // 2. creating order entity.
        Order order = new Order();
        order.setUserID(userDTO.getUserID());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("Pending");
        order.setTotalAmount(BigDecimal.ZERO);

        // 3. Fetch product information from the Inventory microservice and validate availability.
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequestDTO orderItemRequestDTO : orderRequestDTO.getOrderItems()) {
            InventoryResponseDTO inventoryResponseDTO = fetchProductInformation(orderItemRequestDTO.getProductID());
            if (inventoryResponseDTO == null) {
                throw new ProductNotFoundException("Product is not found with ID " + orderItemRequestDTO.getProductID());
            }
            if (orderItemRequestDTO.getQuantity() > inventoryResponseDTO.getStockQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product:" + orderItemRequestDTO.getProductID());
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setProductID(inventoryResponseDTO.getId());
            orderItem.setPricePerUnit(inventoryResponseDTO.getUnitPrice());
            orderItem.setQuantity(orderItemRequestDTO.getQuantity());
            orderItem.setSubTotal(inventoryResponseDTO.getUnitPrice().multiply(BigDecimal.valueOf(orderItemRequestDTO.getQuantity())));
            orderItem.setOrder(order);

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubTotal());
        }
        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        // 4. Deduct the relevant quantities from the Inventory microservice.
        deductInventoryQuantities(orderItems);

        // 5. Save the order and order items
        orderItemRepository.saveAll(orderItems);
        return orderRepository.save(order);
    }

    private UserDTO fetchUserInformation(String userID) {
        WebClient webClient = webClientBuilder.baseUrl("http://user-service").build();
        UserDTO userDTO = webClient.get()
                .uri("/api/users/{userID}", userID)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    throw new UserNotFoundException("User not found for the userID " + userID);
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    throw new MicroserviceException("User microservice is unavailable");
                })
                .bodyToMono(UserDTO.class)
                .block();
        if (userDTO == null) {
            throw new UserNotFoundException("User not found for the userID " + userID );
        }
        return userDTO;
    }

    private InventoryResponseDTO fetchProductInformation(Long productID) {
        WebClient webClient = webClientBuilder.baseUrl("http://inventory-service").build();

        InventoryResponseDTO inventoryResponseDTO = webClient.get()
                .uri("/api/inventory/{productID}", productID)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    throw new ProductNotFoundException("Product is not found with ID " + productID);
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    throw new MicroserviceException("Inventory microservice is unavailable");
                })
                .bodyToMono(InventoryResponseDTO.class)
                .block();

        return inventoryResponseDTO;
    }

    private void deductInventoryQuantities(List<OrderItem> orderItems) {
        WebClient webClient = webClientBuilder.baseUrl("http://inventory-service").build();
        List<Mono<Void>> updateRequests = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            Mono<Void> updateRequest = webClient.put()
                    .uri("api/inventory/{productID}/quan-deduction/{quantityToDeduct}", orderItem.getProductID(),orderItem.getQuantity())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                            throw new ProductNotFoundException("Product not found with the productID : " + orderItem.getProductID());
                        }
                        throw new InsufficientStockException("Insufficient stock for the productID : " + orderItem.getProductID());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                        throw new MicroserviceException("Inventory microservice is unavailable.");
                    })
                    .bodyToMono(Void.class);
            updateRequests.add(updateRequest);
        }
        Flux.merge(updateRequests).blockLast();
    }
}
