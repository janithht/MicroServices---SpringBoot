package com.ADBMS.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderItemResponseDTO {
    private Long productID;
    private int quantityRequired;
    private BigDecimal pricePerUnit;
}
