-- Вставка тестового пользователя
INSERT INTO users (id, email, password, role)
VALUES (1, 'test@test.com', 'password', 'USER');

-- Вставка директорий
INSERT INTO directories (id, name, user_id, parent_id)
VALUES
(1, 'Root Directory', 1, NULL),
(2, 'Documents', 1, 1),
(3, 'Images', 1, 1);

-- Вставка файлов (исправленные UUID)
INSERT INTO files (id, name, uuid, directory_id, user_id)
VALUES
(1, 'readme.pdf', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'::uuid, 2, 1),
(2, 'photo.jpg', 'b1ffcc99-9c0b-4ef8-bb6d-6bb9bd380a12'::uuid, 3, 1),
(3, 'document.txt', 'c2eecc99-9c0b-4ef8-bb6d-6bb9bd380a13'::uuid, 1, 1);