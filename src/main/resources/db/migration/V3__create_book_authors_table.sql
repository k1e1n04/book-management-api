CREATE TABLE book_authors (
      book_id VARCHAR(36) NOT NULL,
      author_id VARCHAR(36) NOT NULL,
      PRIMARY KEY (book_id, author_id),
      FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
      FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE
);
