CREATE TABLE IF NOT EXISTS categories(
    id uuid PRIMARY KEY DEFAULT spring_test.uuid_generate_v4(),
    name text NOT NULL
);

CREATE TABLE IF NOT EXISTS products(
    id uuid PRIMARY KEY DEFAULT spring_test.uuid_generate_v4(),
    name text NOT NULL,
    price numeric(10, 2) NOT NULL CHECK (price >= 0),
    category_id uuid REFERENCES categories (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS carts(
    id uuid PRIMARY KEY DEFAULT spring_test.uuid_generate_v4(),
    created_at timestamp DEFAULT now()
);

CREATE TABLE IF NOT EXISTS products_carts(
    product_id uuid REFERENCES products (id) ON DELETE CASCADE,
    cart_id uuid references carts (id) ON DELETE CASCADE,
    PRIMARY KEY (product_id, cart_id)
);

CREATE TABLE IF NOT EXISTS orders(
    id uuid PRIMARY KEY DEFAULT spring_test.uuid_generate_v4(),
    created_at timestamp DEFAULT now(),
    user_id uuid REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS deliveries(
    id uuid PRIMARY KEY DEFAULT spring_test.uuid_generate_v4(),
    address text NOT NULL,
    order_id uuid UNIQUE REFERENCES orders (id) ON DELETE CASCADE
);