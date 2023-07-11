CREATE TABLE IF NOT EXISTS events(
    id              BIGINT      PRIMARY KEY,
    aggregate_id    UUID        NOT NULL,
    schema_version  INTEGER     NOT NULL,
    timestamp       TIMESTAMP   NOT NULL,
    event_type      TEXT        NOT NULL,
    payload         TEXT        NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS events_seq INCREMENT BY 1 OWNED BY events.id;

CREATE TABLE IF NOT EXISTS read_model_task(
    id      VARCHAR(255)    NOT NULL,
    title   TEXT            NOT NULL,
    status  VARCHAR(255)    NOT NULL
);