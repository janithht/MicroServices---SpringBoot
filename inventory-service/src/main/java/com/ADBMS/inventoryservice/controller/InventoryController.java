package com.ADBMS.inventoryservice.controller;

import com.ADBMS.inventoryservice.dto.*;
import com.ADBMS.inventoryservice.model.Inventory;
import com.ADBMS.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Inventory addNewProduct(@RequestBody ProductCreateDTO productCreateDTO){
       return inventoryService.addNewProduct(productCreateDTO);
    }

    @GetMapping("/{productName}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponseDTO getProductDetailsByName(@PathVariable String productName){
        return inventoryService.getProductDetailsByName(productName);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponseDTO> getAllProducts(){
        return inventoryService.getAllProducts();
    }

    @GetMapping("/orders")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderedProductResponse> getOrderedProducts(@RequestBody List<OrderedProductRequest> orderedProductRequestList) {
        return inventoryService.getOrderedProducts(orderedProductRequestList);
    }

    @PutMapping("/{productName}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponseDTO updateProductByName(@PathVariable String productName,@RequestBody ProductUpdateDTO productUpdateDTO){
        return inventoryService.updateProductByName(productName, productUpdateDTO);
    }

    @DeleteMapping("/{productName}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteProductByName(@PathVariable String productName){
        return inventoryService.deleteProductByName(productName);
    }

}
