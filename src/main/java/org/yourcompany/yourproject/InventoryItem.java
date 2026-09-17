package org.yourcompany.yourproject;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sku;
    private String name;
    private String category;
    private int quantity;
    private int reorderLevel;
    private double unitPrice;
    private String supplier;

    protected InventoryItem() {}

    public InventoryItem(String sku, String name, String category, int quantity, int reorderLevel, double unitPrice, String supplier) {
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
        this.unitPrice = unitPrice;
        this.supplier = supplier;
    }

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getQuantity() { return quantity; }
    public int getReorderLevel() { return reorderLevel; }
    public double getUnitPrice() { return unitPrice; }
    public String getSupplier() { return supplier; }
    public void adjustQuantity(int amount) {
        if (quantity + amount < 0) throw new IllegalArgumentException("Stock cannot be negative");
        quantity += amount;
    }
    public String getStatus() {
        if (quantity == 0) return "Out of stock";
        if (quantity <= reorderLevel) return "Low stock";
        return "In stock";
    }
}