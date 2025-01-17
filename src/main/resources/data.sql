INSERT INTO orders (customer_name, status, total_price, deleted) VALUES
                                                                     ('John Doe', 'PENDING', 100.00, false),
                                                                     ('Jane Smith', 'CONFIRMED', 150.50, false);

INSERT INTO products (order_id, name, price, quantity) VALUES
                                                           (1, 'Product A', 50.00, 1),
                                                           (1, 'Product B', 50.00, 1),
                                                           (2, 'Product C', 75.25, 2);

