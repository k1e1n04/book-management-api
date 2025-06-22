INSERT INTO books (id, title, price, publication_status) VALUES
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '吾輩は猫である', 1200, 'UNPUBLISHED'),
  ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '羅生門', 900, 'PUBLISHED');

INSERT INTO book_authors (book_id, author_id) VALUES
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111'),
  ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222'),
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '22222222-2222-2222-2222-222222222222');
