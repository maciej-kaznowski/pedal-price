--  Create a table for Bucket4j: https://bucket4j.com/8.7.0/toc.html#jdbc-integrations
CREATE TABLE IF NOT EXISTS buckets
(
    id    BIGINT PRIMARY KEY,
    state BYTEA
);