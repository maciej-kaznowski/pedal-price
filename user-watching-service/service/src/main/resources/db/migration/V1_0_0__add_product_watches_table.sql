CREATE TABLE IF NOT EXISTS product_watches
(
    id                                   UUID PRIMARY KEY,

    name_value                           TEXT          NULL,
    name_type                            TEXT          NULL,

    brand_value                          TEXT          NULL,
    brand_type                           TEXT          NULL,

    category_value                       TEXT          NULL,
    category_type                        TEXT          NULL,

    price_discount_min_percent_inclusive DECIMAL(8, 2) NULL,
    price_absolute_amount                DECIMAL(5, 2) NULL,
    price_absolute_currency              CHAR(3)       NULL,

    created_at                           TIMESTAMP     NOT NULL,
    updated_at                           TIMESTAMP     NOT NULL
)