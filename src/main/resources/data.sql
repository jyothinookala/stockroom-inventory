MERGE INTO inventory_items (sku, name, category, quantity, reorder_level, unit_price, supplier)
KEY (sku)
VALUES ('WH-1001', 'Arcade Console', 'Electronics', 24, 10, 249.00, 'Northstar Supply');

MERGE INTO inventory_items (sku, name, category, quantity, reorder_level, unit_price, supplier)
KEY (sku)
VALUES ('WH-1002', 'Wireless Controller', 'Accessories', 8, 12, 49.50, 'Northstar Supply');

MERGE INTO inventory_items (sku, name, category, quantity, reorder_level, unit_price, supplier)
KEY (sku)
VALUES ('WH-1003', 'USB-C Charging Dock', 'Accessories', 42, 15, 32.00, 'Orbit Goods');

MERGE INTO inventory_items (sku, name, category, quantity, reorder_level, unit_price, supplier)
KEY (sku)
VALUES ('WH-1004', 'Studio Headset', 'Audio', 6, 8, 89.00, 'Signal House');

MERGE INTO inventory_items (sku, name, category, quantity, reorder_level, unit_price, supplier)
KEY (sku)
VALUES ('WH-1005', 'Capture Card Pro', 'Streaming', 18, 6, 159.00, 'Signal House');

MERGE INTO inventory_items (sku, name, category, quantity, reorder_level, unit_price, supplier)
KEY (sku)
VALUES ('WH-1006', 'Mechanical Keyboard', 'Accessories', 31, 10, 119.00, 'Orbit Goods');