DELETE FROM products;
DELETE FROM orders;

INSERT INTO orders (order_id, customer_name, status, total_price, deleted) VALUES
                                                                               (1, 'John Doe', 'PENDING', 100.00, false),
                                                                               (2, 'Jane Smith', 'CONFIRMED', 150.50, false);

INSERT INTO products (product_id, order_id, name, price, quantity) VALUES
                                                                       (1, 1, 'Product A', 50.00, 1),
                                                                       (2, 1, 'Product B', 50.00, 1),
                                                                       (3, 2, 'Product C', 75.25, 2);