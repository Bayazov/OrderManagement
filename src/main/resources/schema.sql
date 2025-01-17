CREATE TABLE IF NOT EXISTS orders (
                                      order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      customer_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    deleted BOOLEAN DEFAULT FALSE
    );

CREATE TABLE IF NOT EXISTS products (
                                        product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        order_id BIGINT,
                                        name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
    );

CREATE INDEX IF NOT EXISTS idx_orders_deleted ON orders(deleted);
