package com.ADBMS.orderservice.service;

import com.ADBMS.orderservice.dto.InventoryResponse;
import com.ADBMS.orderservice.dto.OrderItemRequestDTO;
import com.ADBMS.orderservice.dto.OrderLineItemsDto;
import com.ADBMS.orderservice.dto.OrderRequestDTO;
import com.ADBMS.orderservice.model.Order;
import com.ADBMS.orderservice.model.OrderItem;
import com.ADBMS.orderservice.model.OrderLineItems;
import com.ADBMS.orderservice.repository.OrderItemRepository;
import com.ADBMS.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final WebClient.Builder webClientBuilder;

    public Order placeOrder(OrderRequestDTO orderRequest){
        // :todo validate user
        // :todo get the set of productIDs and set of quantities to the inventory service
        // :todo if every thing is okay place the order
        Order order = new Order();
        order.setUserID(orderRequest.getUserID());

//        List<OrderItem> orderItems = orderRequest.getOrderItems()
//                .stream()
//                .map(item -> )
//                .toList();
//        order.setOrderLineItemsList(orderLineItems);

        List<OrderItemRequestDTO> orderItems = orderRequest.getOrderItems();

        // Call Inventory Service, and place order if product is in
        // stock
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory/orders", uriBuilder -> uriBuilder.queryParam("orderItems",orderItems).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        assert inventoryResponseArray != null;
        boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponse::isInStock);

        if(allProductsInStock){
            order = orderRepository.save(order);
            BigDecimal totalAmount = BigDecimal.valueOf(0);
            for (InventoryResponse inventoryResponse:
                 inventoryResponseArray) {
                OrderItem orderItem = new OrderItem();
                orderItem.setProductID(inventoryResponse.getProductID());
                orderItem.setQuantity(inventoryResponse.getQuantityRequired());
                orderItem.setPricePerUnit(inventoryResponse.getUnitPrice());
                orderItem.setSubTotal(BigDecimal.valueOf(inventoryResponse.getQuantityRequired()).multiply(inventoryResponse.getUnitPrice()));
                orderItem.setOrder(order);
                orderItemRepository.save(orderItem);
                totalAmount = totalAmount.add(orderItem.getSubTotal());
            }
            order.setStatus("Processed");
            order.setTotalAmount(totalAmount);
            order.setOrderDate(LocalDateTime.now());
            return orderRepository.save(order);
        }else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }
    }

//    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
//        OrderLineItems orderLineItems = new OrderLineItems();
//        orderLineItems.setPrice(orderLineItemsDto.getPrice());
//        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
//        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
//        return orderLineItems;
//    }


}
