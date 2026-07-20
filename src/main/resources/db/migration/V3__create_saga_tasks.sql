ALTER TABLE users
    ADD COLUMN enrichment_status text NOT NULL DEFAULT 'PENDING';

CREATE TABLE IF NOT EXISTS saga_tasks
(
    id                   uuid PRIMARY KEY                  DEFAULT spring_test.uuid_generate_v4(),
    user_id              uuid                     NOT NULL,
    status               text                     NOT NULL DEFAULT 'PENDING',
    discount_card_number text                     NOT NULL,
    balance              numeric(10, 2)           NOT NULL,
    attempts             integer                  NOT NULL DEFAULT 0,
    created_at           timestamp WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           timestamp WITH TIME ZONE          DEFAULT CURRENT_TIMESTAMP,
    is_deleted           boolean                  NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_saga_tasks_user FOREIGN KEY (user_id) REFERENCES spring_test.users (id)
);

CREATE INDEX idx_saga_tasks_status ON saga_tasks (status);