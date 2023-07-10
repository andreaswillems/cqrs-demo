CREATE TABLE IF NOT EXISTS base_event(
    event_id        UUID        PRIMARY KEY,
    aggregate_id    UUID        NOT NULL,
    --version         INTEGER     NOT NULL,
    timestamp       TIMESTAMP   NOT NULL,
    event_type      TEXT        NOT NULL,
    event_type_id   INTEGER     NOT NULL,
    payload         TEXT        NOT NULL
);

CREATE TABLE IF NOT EXISTS read_model_task(
    id      VARCHAR(255)    NOT NULL,
    title   TEXT            NOT NULL,
    status  VARCHAR(255)    NOT NULL
);