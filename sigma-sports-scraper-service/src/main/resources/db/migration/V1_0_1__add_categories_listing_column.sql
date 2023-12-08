ALTER TABLE categories
    ADD COLUMN IF NOT EXISTS listing BOOLEAN;

UPDATE categories
SET listing = TRUE;

ALTER TABLE categories
    ALTER COLUMN listing SET NOT NULL;
