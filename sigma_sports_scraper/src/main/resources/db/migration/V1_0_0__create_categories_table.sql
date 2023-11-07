CREATE TABLE IF NOT EXISTS categories
(
    id         UUID PRIMARY KEY,
    url        TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
)