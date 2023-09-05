package com.ADBMS.inventoryservice.service;

import com.ADBMS.inventoryservice.dto.*;
import com.ADBMS.inventoryservice.model.Inventory;
import com.ADBMS.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public Inventory addNewProduct(ProductCreateDTO productCreateDTO) {
        Inventory inventory = Inventory.builder()
                .productName(productCreateDTO.getProductName())
                .stockQuantity(productCreateDTO.getStockQuantity())
                .description(productCreateDTO.getDescription())
                .price(productCreateDTO.getPrice())
                .build();
        Inventory newProd = inventoryRepository.save(inventory);
        return newProd;
    }

    public ProductResponseDTO getProductDetailsByName(String productName) {
        Inventory inventory = inventoryRepository.findInventoryByProductName(productName);
        if(inventory == null){
            throw new IllegalArgumentException("Product not Found");
        }

        ProductResponseDTO productResponse = new ProductResponseDTO();
        productResponse.setId(inventory.getProductID());
        productResponse.setProductName(inventory.getProductName());
        productResponse.setDescription(inventory.getDescription());
        productResponse.setUnitPrice(inventory.getPrice());
        productResponse.setStockQuantity(inventory.getStockQuantity());

        return  productResponse;
    }

    public ProductResponseDTO updateProductByName(String productName , ProductUpdateDTO productUpdateDTO) {
        Inventory existingProduct = inventoryRepository.findInventoryByProductName(productName);
        if(existingProduct == null){
            throw new IllegalArgumentException("Product not Found");
        }
        existingProduct.setProductName(productUpdateDTO.getProductName());
        existingProduct.setStockQuantity(productUpdateDTO.getStockQuantity());
        existingProduct.setPrice(productUpdateDTO.getUnitPrice());
        existingProduct.setDescription(productUpdateDTO.getDescription());
        Inventory updatedInventory = inventoryRepository.save(existingProduct);

        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(updatedInventory.getProductID());
        productResponseDTO.setProductName(updatedInventory.getProductName());
        productResponseDTO.setDescription(updatedInventory.getDescription());
        productResponseDTO.setStockQuantity(updatedInventory.getStockQuantity());
        productResponseDTO.setUnitPrice(updatedInventory.getPrice());

        return productResponseDTO;
    }

    public String deleteProductByName(String productName) {
        Inventory existingProduct = inventoryRepository.findInventoryByProductName(productName);
        if(existingProduct == null){
            throw new IllegalArgumentException("Product not Found");
        }
        inventoryRepository.delete(existingProduct);
        return "Product deleted successfully";
    }

    public List<ProductResponseDTO> getAllProducts() {
        List<Inventory> products = inventoryRepository.findAll();

        List<ProductResponseDTO> productResponseDTOs = new ArrayList<>();

        for (Inventory product : products) {
            ProductResponseDTO productResponseDTO = new ProductResponseDTO();
            productResponseDTO.setProductName(product.getProductName());
            productResponseDTO.setId(product.getProductID());
            productResponseDTO.setStockQuantity(product.getStockQuantity());
            productResponseDTO.setUnitPrice(product.getPrice());
            productResponseDTO.setDescription(product.getDescription());
            productResponseDTOs.add(productResponseDTO);
        }

        return productResponseDTOs;
    }

    public List<OrderedProductResponse> getOrderedProducts(List<OrderedProductRequest> orderedProductRequestList) {
        List<OrderedProductResponse> orderedProductResponses = new ArrayList<>();

        for (OrderedProductRequest orderedProduct:
             orderedProductRequestList) {
            OrderedProductResponse orderedProductResponse = new OrderedProductResponse();
            Inventory inventory = inventoryRepository.findById(orderedProduct.getProductID()).orElse(null);

            if (inventory == null || inventory.getStockQuantity() < orderedProduct.getQuantity()) {
                orderedProductResponse.setInStock(false);
                continue;
            }

            orderedProductResponse.setInStock(true);
            orderedProductResponse.setProductID(inventory.getProductID());
            orderedProductResponse.setUnitPrice(inventory.getPrice());
            orderedProductResponse.setQuantityRequired(orderedProduct.getQuantity());
            inventory.setStockQuantity(inventory.getStockQuantity()-orderedProduct.getQuantity());
            orderedProductResponses.add(orderedProductResponse);
        }
        return orderedProductResponses;
    }
}
