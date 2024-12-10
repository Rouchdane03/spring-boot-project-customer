ALTER TABLE customer
ADD COLUMN gender VARCHAR(8);

ALTER TABLE customer
ALTER COLUMN gender SET NOT NULL;

ALTER TABLE customer
ADD CONSTRAINT check_customer_gender CHECK (gender IN ('MALE', 'FEMALE'));