CREATE TYPE publication_status AS ENUM ('UNPUBLISHED', 'PUBLISHED');

CREATE TABLE books (
   id VARCHAR(36) PRIMARY KEY,
   title VARCHAR(255) NOT NULL,
   price INTEGER NOT NULL CHECK (price >= 0 AND price <= 1000000),
   publication_status publication_status NOT NULL
);
