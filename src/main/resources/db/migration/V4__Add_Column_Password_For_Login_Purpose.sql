ALTER TABLE customer
    ADD COLUMN password TEXT;

ALTER TABLE customer
    ALTER COLUMN password SET NOT NULL;