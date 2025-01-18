DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL
);

CREATE TABLE orders (
                        order_id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT NOT NULL,
                        customer_name VARCHAR(255) NOT NULL,
                        status VARCHAR(50) NOT NULL,
                        total_price DECIMAL(10, 2) NOT NULL,
                        deleted BOOLEAN DEFAULT FALSE,
                        FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE products (
                          product_id INT AUTO_INCREMENT PRIMARY KEY,
                          order_id INT,
                          name VARCHAR(255) NOT NULL,
                          price DECIMAL(10, 2) NOT NULL,
                          quantity INT NOT NULL,
                          FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_orders_deleted ON orders(deleted);
CREATE INDEX IF NOT EXISTS idx_orders_user ON orders(user_id);