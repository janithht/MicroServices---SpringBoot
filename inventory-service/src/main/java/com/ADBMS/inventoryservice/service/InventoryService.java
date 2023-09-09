package com.ADBMS.inventoryservice.service;

import com.ADBMS.inventoryservice.dto.ProductCreateDTO;
import com.ADBMS.inventoryservice.dto.ProductResponseDTO;
import com.ADBMS.inventoryservice.dto.ProductUpdateDTO;
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
                .quantity(productCreateDTO.getQuantity())
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
        productResponse.setId(inventory.getId());
        productResponse.setProductName(inventory.getProductName());
        productResponse.setQuantity(inventory.getQuantity());

        return  productResponse;

    }

    public ProductResponseDTO updateProductByName(String productName , ProductUpdateDTO productUpdateDTO) {
        Inventory existingProduct = inventoryRepository.findInventoryByProductName(productName);
        if(existingProduct == null){
            throw new IllegalArgumentException("Product not Found");
        }
        existingProduct.setProductName(productUpdateDTO.getProductName());
        existingProduct.setQuantity(productUpdateDTO.getQuantity());

        Inventory updatedInventory = inventoryRepository.save(existingProduct);

        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(updatedInventory.getId());
        productResponseDTO.setProductName(updatedInventory.getProductName());
        productResponseDTO.setQuantity(updatedInventory.getQuantity());

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
            productResponseDTO.setId(product.getId());
            productResponseDTO.setQuantity(product.getQuantity());
            productResponseDTOs.add(productResponseDTO);
        }
        return productResponseDTOs;
    }

    public Boolean isInStock(Long productId){
        Inventory existingProduct = inventoryRepository.findInventoryByProductId(productId);
        if (existingProduct==null){
            return false;
        }
        else{
            return true;
        }
    }

}
