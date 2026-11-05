-- Data contoh PasarKita. Dijalankan lewat `task seed SCALE=S|M`.
-- Variabel psql: seed, sellers, products, customers, orders.
\timing on

SELECT setseed(:seed);

TRUNCATE shipment, loyalty_point, invoice, order_line, orders, cart_item, wallet, stock, customer, product, seller
    RESTART IDENTITY CASCADE;

-- Penjual. Penjual nomor 1 adalah penjual raksasa (sekitar 10% produk).
INSERT INTO seller (name, city)
SELECT 'Toko ' || g,
       (ARRAY ['Bandung','Jakarta','Surabaya','Cimahi','Medan','Makassar'])[1 + floor(random() * 6)::int]
FROM generate_series(1, :sellers) g;
UPDATE seller SET name = 'Toko Raksasa' WHERE id = 1;

INSERT INTO product (seller_id, sku, name, description, price)
SELECT CASE WHEN g % 10 = 0 THEN 1 ELSE 1 + (g % :sellers) END,
       'SKU-' || lpad(g::text, 7, '0'),
       (ARRAY ['Kopi','Teh','Sambal','Keripik','Batik','Tas','Sepatu','Kemeja','Madu','Gula Aren'])[1 + floor(random() * 10)::int]
           || ' ' || (ARRAY ['Premium','Asli','Spesial','Kemasan Besar','Original'])[1 + floor(random() * 5)::int]
           || ' #' || g,
       'Produk contoh nomor ' || g,
       round((10000 + random() * 490000)::numeric, -2)
FROM generate_series(1, :products) g;

INSERT INTO stock (product_id, quantity)
SELECT id, floor(random() * 500)::int FROM product;

-- Semua customer memakai password rahasia123. Hash dihitung sekali agar seed tidak lambat.
WITH h AS (SELECT crypt('rahasia123', gen_salt('bf', 10)) AS hash)
INSERT INTO customer (email, name, password_hash, credit_limit)
SELECT 'user' || g || '@pasarkita.test', 'Pelanggan ' || g, h.hash, 5000000
FROM generate_series(1, :customers) g, h;

INSERT INTO wallet (customer_id, balance)
SELECT id, 10000000 FROM customer;

INSERT INTO orders (customer_id, status, total, tax, created_at)
SELECT 1 + floor(random() * :customers)::int,
       (ARRAY ['PAID','PAID','PAID','SHIPPED','SHIPPED','DELIVERED','CANCELLED'])[1 + floor(random() * 7)::int],
       0, 0,
       now() - random() * interval '180 days'
FROM generate_series(1, :orders) g;

WITH x AS MATERIALIZED (
    SELECT o.id                                     AS order_id,
           1 + floor(random() * :products)::bigint  AS product_id,
           1 + floor(random() * 3)::int             AS qty
    FROM orders o
             CROSS JOIN LATERAL generate_series(1, 1 + (o.id % 4)::int) n
)
INSERT INTO order_line (order_id, product_id, quantity, unit_price)
SELECT x.order_id, p.id, x.qty, p.price
FROM x
         JOIN product p ON p.id = x.product_id;

UPDATE orders o
SET total = round(s.amount * 1.11, 2),
    tax   = round(s.amount * 0.11, 2)
FROM (SELECT order_id, sum(quantity * unit_price) AS amount FROM order_line GROUP BY order_id) s
WHERE s.order_id = o.id;

INSERT INTO invoice (order_id, seller_id, amount, issued_at)
SELECT ol.order_id, p.seller_id, sum(ol.quantity * ol.unit_price), min(o.created_at)
FROM order_line ol
         JOIN product p ON p.id = ol.product_id
         JOIN orders o ON o.id = ol.order_id
WHERE o.status <> 'CANCELLED'
GROUP BY ol.order_id, p.seller_id;

ANALYZE;
SELECT pg_stat_statements_reset();

SELECT 'seed selesai'                      AS status,
       (SELECT count(*) FROM product)      AS produk,
       (SELECT count(*) FROM customer)     AS customer,
       (SELECT count(*) FROM orders)       AS orders,
       (SELECT count(*) FROM invoice)      AS invoice;
