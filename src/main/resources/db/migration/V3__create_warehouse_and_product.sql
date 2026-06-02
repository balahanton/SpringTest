CREATE TABLE IF NOT EXISTS spring_test.warehouses
(
    id         UUID PRIMARY KEY                  DEFAULT spring_test.uuid_generate_v4(),
    name       TEXT                     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN                  NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS spring_test.products
(
    id         UUID PRIMARY KEY                  DEFAULT spring_test.uuid_generate_v4(),
    title      TEXT                     NOT NULL,
    price      NUMERIC(12, 2)           NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN                  NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS spring_test.warehouses_products
(
    warehouse_id UUID NOT NULL,
    product_id   UUID NOT NULL,

    PRIMARY KEY (warehouse_id, product_id),
    CONSTRAINT fk_wp_warehouse FOREIGN KEY (warehouse_id) REFERENCES spring_test.warehouses (id) ON DELETE CASCADE,
    CONSTRAINT fk_wp_product FOREIGN KEY (product_id) REFERENCES spring_test.products (id) ON DELETE CASCADE
);