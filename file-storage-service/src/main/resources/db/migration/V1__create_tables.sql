CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS directories
(
    id    BIGSERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(200)          NOT NULL,
    user_id BIGINT,
    parent_id BIGINT REFERENCES directories(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS files
(
    id BIGSERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(200) NOT NULL,
    uuid  UUID DEFAULT uuid_generate_v4() NOT NULL,
    directory BIGINT REFERENCES directories(id) ON DELETE CASCADE,
    user_id BIGINT,
    UNIQUE (name, directory)
);
