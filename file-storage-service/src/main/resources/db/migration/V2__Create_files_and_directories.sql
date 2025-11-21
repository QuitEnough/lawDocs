CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE file.directories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    parent_id BIGINT REFERENCES file.directories(id) ON DELETE CASCADE
);

CREATE TABLE file.files (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    uuid UUID DEFAULT uuid_generate_v4() NOT NULL,
    directory BIGINT REFERENCES file.directories(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL,
    UNIQUE (name, directory)
);