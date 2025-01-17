ALTER TABLE orders ADD COLUMN deleted BOOLEAN DEFAULT FALSE;
CREATE INDEX idx_orders_deleted ON orders(deleted);