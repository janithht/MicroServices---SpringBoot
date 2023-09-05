package com.ADBMS.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderedProductResponse {
    private boolean isInStock;
    private Long productID;
    private int quantityRequired;
    private BigDecimal unitPrice;
}
