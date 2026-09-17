package org.yourcompany.yourproject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryItemRepository repository;

    public InventoryController(InventoryItemRepository repository) { this.repository = repository; }

    @GetMapping
    public List<InventoryItem> list(@RequestParam(required = false) String search) {
        return search == null || search.isBlank() ? repository.findAll() : repository.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(search, search);
    }

    @GetMapping("/summary")
    public Map<String, Object> summary() {
        List<InventoryItem> items = repository.findAll();
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalItems", items.size());
        summary.put("unitsOnHand", items.stream().mapToInt(InventoryItem::getQuantity).sum());
        summary.put("lowStock", items.stream().filter(item -> item.getQuantity() <= item.getReorderLevel()).count());
        summary.put("inventoryValue", items.stream().mapToDouble(item -> item.getQuantity() * item.getUnitPrice()).sum());
        return summary;
    }

    @PostMapping
    public InventoryItem create(@RequestBody InventoryItemRequest request) {
        return repository.save(new InventoryItem(request.sku(), request.name(), request.category(), request.quantity(), request.reorderLevel(), request.unitPrice(), request.supplier()));
    }

    @PatchMapping("/{id}/adjust")
    public ResponseEntity<InventoryItem> adjust(@PathVariable Long id, @RequestBody AdjustmentRequest request) {
        return repository.findById(id).map(item -> {
            item.adjustQuantity(request.amount());
            return ResponseEntity.ok(repository.save(item));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public record InventoryItemRequest(String sku, String name, String category, int quantity, int reorderLevel, double unitPrice, String supplier) {}
    public record AdjustmentRequest(int amount) {}
}