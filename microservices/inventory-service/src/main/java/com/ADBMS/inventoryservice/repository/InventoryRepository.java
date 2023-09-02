package com.ADBMS.inventoryservice.repository;
import com.ADBMS.inventoryservice.model.Inventory;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;


public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Inventory findInventoryByProductName(String productName);
}
