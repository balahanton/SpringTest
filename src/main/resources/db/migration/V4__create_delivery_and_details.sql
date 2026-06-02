CREATE TABLE IF NOT EXISTS spring_test.deliveries
(
    id         UUID PRIMARY KEY                  DEFAULT spring_test.uuid_generate_v4(),
    address    TEXT                     NOT NULL,
    status     TEXT                     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN                  NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS spring_test.delivery_details
(
    id             UUID PRIMARY KEY                  DEFAULT spring_test.uuid_generate_v4(),
    delivery_id    UUID                     NOT NULL UNIQUE,
    courier_name   TEXT                     NOT NULL,
    delivery_notes TEXT,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted     BOOLEAN                  NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_details_delivery FOREIGN KEY (delivery_id) REFERENCES spring_test.deliveries (id)
);

CREATE INDEX IF NOT EXISTS idx_delivery_details_delivery_id ON spring_test.delivery_details (delivery_id);