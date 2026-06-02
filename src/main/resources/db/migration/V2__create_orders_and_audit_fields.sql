ALTER TABLE spring_test.users
    ADD COLUMN created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE spring_test.users
    ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE spring_test.users
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE IF NOT EXISTS spring_test.orders
(
    id          UUID PRIMARY KEY                  DEFAULT spring_test.uuid_generate_v4(),
    description TEXT,
    user_id     UUID                     NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted  BOOLEAN                  NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES spring_test.users (id)
);

CREATE INDEX IF NOT EXISTS idx_orders_user_id ON spring_test.orders (user_id);