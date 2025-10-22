CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS users
(
    id       BIGSERIAL PRIMARY KEY NOT NULL,
    email    VARCHAR(200) UNIQUE   NOT NULL,
    password VARCHAR(200)          NOT NULL,
    role     VARCHAR(200)
    );

CREATE TABLE IF NOT EXISTS directories
(
    id    BIGSERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(200)          NOT NULL,
    user_id BIGINT REFERENCES users(id),
    parent_id BIGINT REFERENCES directories(id),
    UNIQUE (id, user_id, parent_id)
    );

CREATE TABLE IF NOT EXISTS files
(
    id BIGSERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(200) NOT NULL UNIQUE,
    uuid  UUID DEFAULT uuid_generate_v4() NOT NULL,
    directory_id BIGINT REFERENCES directories(id),
    user_id BIGINT REFERENCES users(id)
    );
