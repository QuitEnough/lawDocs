CREATE TABLE IF NOT EXISTS users
(
    id       BIGSERIAL PRIMARY KEY NOT NULL,
    email    VARCHAR(200) UNIQUE   NOT NULL,
    password VARCHAR(200)          NOT NULL,
    role     VARCHAR(200)
);
