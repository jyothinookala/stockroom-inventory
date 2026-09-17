package org.yourcompany.yourproject;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(String name, String sku);
}