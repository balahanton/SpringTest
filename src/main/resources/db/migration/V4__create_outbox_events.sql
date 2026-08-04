CREATE TABLE spring_test.outbox_events
(
    id              UUID PRIMARY KEY,
    event_type      VARCHAR(100) NOT NULL,
    aggregate_id    UUID         NOT NULL,
    payload         TEXT         NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'NEW',
    attempts        INTEGER      NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    sent_at         TIMESTAMPTZ,
    claimed_at      TIMESTAMPTZ,
    next_attempt_at TIMESTAMPTZ
);

CREATE INDEX idx_outbox_events_status_created_at
    ON spring_test.outbox_events (status, created_at)
    WHERE status = 'NEW';