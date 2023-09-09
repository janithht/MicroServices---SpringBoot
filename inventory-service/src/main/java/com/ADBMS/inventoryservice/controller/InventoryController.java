package com.ADBMS.inventoryservice.controller;

import com.ADBMS.inventoryservice.dto.ProductCreateDTO;
import com.ADBMS.inventoryservice.dto.ProductResponseDTO;
import com.ADBMS.inventoryservice.dto.ProductUpdateDTO;
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

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@PathVariable("productId") String productId){
        return inventoryService.isInStock(Long.valueOf(productId));
    }

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
