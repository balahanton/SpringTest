ALTER TABLE users
    ADD COLUMN created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE users
    ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE users
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE IF NOT EXISTS products(
    id uuid PRIMARY KEY DEFAULT spring_test.uuid_generate_v4(),
    title text NOT NULL,
    price numeric(10, 2) NOT NULL CHECK (price >= 0),
    created_at timestamp WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted boolean NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS warehouses(
    id uuid PRIMARY KEY DEFAULT spring_test.uuid_generate_v4(),
    name text NOT NULL,
    created_at timestamp WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted boolean NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS warehouses_products(
    warehouse_id uuid NOT NULL,
    product_id   uuid NOT NULL,

    PRIMARY KEY (warehouse_id, product_id),
    CONSTRAINT fk_wp_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses (id) ON DELETE CASCADE,
    CONSTRAINT fk_wp_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS orders(
    id uuid PRIMARY KEY DEFAULT spring_test.uuid_generate_v4(),
    description text,
    user_id uuid NOT NULL,
    created_at timestamp WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted boolean NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES spring_test.users (id)
);

CREATE TABLE IF NOT EXISTS deliveries(
    id uuid PRIMARY KEY DEFAULT spring_test.uuid_generate_v4(),
    address text NOT NULL,
    status text NOT NULL,
    created_at timestamp WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted boolean NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS delivery_details
(
    id uuid PRIMARY KEY DEFAULT spring_test.uuid_generate_v4(),
    delivery_id uuid NOT NULL UNIQUE,
    courier_name text NOT NULL,
    delivery_notes text,
    created_at timestamp WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted boolean NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_details_delivery FOREIGN KEY (delivery_id) REFERENCES spring_test.deliveries (id)
);