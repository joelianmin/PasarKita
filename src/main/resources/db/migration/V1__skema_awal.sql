CREATE TABLE seller (
    id         bigserial PRIMARY KEY,
    name       varchar(200) NOT NULL,
    city       varchar(100),
    created_at timestamptz  NOT NULL DEFAULT now()
);

CREATE TABLE product (
    id          bigserial PRIMARY KEY,
    seller_id   bigint        NOT NULL REFERENCES seller (id),
    sku         varchar(40)   NOT NULL,
    name        varchar(200)  NOT NULL,
    description text,
    price       numeric(12, 2) NOT NULL,
    created_at  timestamptz   NOT NULL DEFAULT now()
);

CREATE TABLE stock (
    product_id bigint PRIMARY KEY REFERENCES product (id),
    quantity   int    NOT NULL
);

CREATE TABLE customer (
    id            bigserial PRIMARY KEY,
    email         varchar(200)   NOT NULL,
    name          varchar(200)   NOT NULL,
    password_hash varchar(100)   NOT NULL,
    credit_limit  numeric(14, 2) NOT NULL DEFAULT 0,
    created_at    timestamptz    NOT NULL DEFAULT now()
);
CREATE INDEX idx_customer_email ON customer (email);

CREATE TABLE wallet (
    customer_id bigint PRIMARY KEY REFERENCES customer (id),
    balance     numeric(14, 2) NOT NULL
);

CREATE TABLE cart_item (
    id          bigserial PRIMARY KEY,
    customer_id bigint      NOT NULL,
    product_id  bigint      NOT NULL,
    quantity    int         NOT NULL,
    created_at  timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE orders (
    id          bigserial PRIMARY KEY,
    customer_id bigint         NOT NULL REFERENCES customer (id),
    status      varchar(20)    NOT NULL,
    total       numeric(14, 2) NOT NULL,
    tax         numeric(14, 2) NOT NULL,
    created_at  timestamptz    NOT NULL DEFAULT now()
);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_customer ON orders (customer_id);
CREATE INDEX idx_orders_created ON orders (created_at);

CREATE TABLE order_line (
    id         bigserial PRIMARY KEY,
    order_id   bigint         NOT NULL REFERENCES orders (id),
    product_id bigint         NOT NULL REFERENCES product (id),
    quantity   int            NOT NULL,
    unit_price numeric(12, 2) NOT NULL
);
CREATE INDEX idx_order_line_order ON order_line (order_id);

CREATE TABLE invoice (
    id        bigserial PRIMARY KEY,
    order_id  bigint         NOT NULL,
    seller_id bigint         NOT NULL,
    amount    numeric(14, 2) NOT NULL,
    issued_at timestamptz    NOT NULL DEFAULT now()
);
CREATE INDEX idx_invoice_seller ON invoice (seller_id);

CREATE TABLE loyalty_point (
    id          bigserial PRIMARY KEY,
    customer_id bigint      NOT NULL,
    order_id    bigint      NOT NULL,
    points      int         NOT NULL,
    created_at  timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE shipment (
    id         bigserial PRIMARY KEY,
    order_id   bigint      NOT NULL,
    status     varchar(20) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE feature_flag (
    name    varchar(100) PRIMARY KEY,
    enabled boolean      NOT NULL
);
INSERT INTO feature_flag (name, enabled) VALUES ('seller.dynamic-thumbnail', false);
