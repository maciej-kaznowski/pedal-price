CREATE TABLE IF NOT EXISTS listing_products
(
    id             UUID PRIMARY KEY,
    name           TEXT          NOT NULL,
    brand          TEXT          NOT NULL,
    category       TEXT          NOT NULL,
    image_url      TEXT          NOT NULL,
    variable_price BOOLEAN       NOT NULL,
    price_amount   DECIMAL(8, 2) NOT NULL,
    price_currency CHAR(3)       NOT NULL,
    rrp_amount     DECIMAL(8, 2) NULL,
    rrp_currency   CHAR(3)       NULL,

    created_at     TIMESTAMP     NOT NULL,
    updated_at     TIMESTAMP     NOT NULL
)