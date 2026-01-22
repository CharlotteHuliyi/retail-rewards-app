-- ==========================================
-- 1. INSERT CUSTOMERS
-- Matches Entity: customerId, firstName, lastName
-- ==========================================
INSERT INTO customer (customer_id, first_name, last_name) VALUES (1001, 'Alice', 'Regular');
INSERT INTO customer (customer_id, first_name, last_name) VALUES (1002, 'Bob', 'BigSpender');
INSERT INTO customer (customer_id, first_name, last_name) VALUES (1003, 'Charlie', 'Cheapskate');
INSERT INTO customer (customer_id, first_name, last_name) VALUES (1004, 'Eddie', 'EdgeCase');
INSERT INTO customer (customer_id, first_name, last_name) VALUES (1005, 'Dora', 'Dormant');

-- ==========================================
-- 2. INSERT TRANSACTIONS
-- Matches Entity: customer_id, amount, date
-- ==========================================

-- --- Customer 1001: Alice (Standard Mix) ---
INSERT INTO transaction (customer_id, amount, date)
VALUES (1001, 120.00, CURRENT_DATE);

INSERT INTO transaction (customer_id, amount, date)
VALUES (1001, 80.00, DATEADD('DAY', -2, CURRENT_DATE));

INSERT INTO transaction (customer_id, amount, date)
VALUES (1001, 60.00, DATEADD('MONTH', -1, CURRENT_DATE));

INSERT INTO transaction (customer_id, amount, date)
VALUES (1001, 150.00, DATEADD('MONTH', -2, CURRENT_DATE));


-- --- Customer 1002: Bob (Big Purchases) ---
INSERT INTO transaction (customer_id, amount, date)
VALUES (1002, 500.00, CURRENT_DATE);

INSERT INTO transaction (customer_id, amount, date)
VALUES (1002, 300.00, DATEADD('MONTH', -1, CURRENT_DATE));


-- --- Customer 1003: Charlie (Low Spender) ---
INSERT INTO transaction (customer_id, amount, date)
VALUES (1003, 50.00, CURRENT_DATE);

INSERT INTO transaction (customer_id, amount, date)
VALUES (1003, 49.99, DATEADD('MONTH', -1, CURRENT_DATE));

INSERT INTO transaction (customer_id, amount, date)
VALUES (1003, 10.00, DATEADD('MONTH', -2, CURRENT_DATE));


-- --- Customer 1004: Eddie (Edge Cases) ---
INSERT INTO transaction (customer_id, amount, date)
VALUES (1004, 100.00, CURRENT_DATE);

INSERT INTO transaction (customer_id, amount, date)
VALUES (1004, 101.00, DATEADD('DAY', -1, CURRENT_DATE));

INSERT INTO transaction (customer_id, amount, date)
VALUES (1004, 51.00, DATEADD('MONTH', -1, CURRENT_DATE));


-- --- Old Data (Ignored by 3-month logic) ---
INSERT INTO transaction (customer_id, amount, date)
VALUES (1001, 200.00, DATEADD('MONTH', -4, CURRENT_DATE));