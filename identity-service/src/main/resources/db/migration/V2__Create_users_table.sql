CREATE TABLE user_schema.users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(200) UNIQUE NOT NULL,
    password VARCHAR(200) NOT NULL,
    create_date TIMESTAMP(6) WITHOUT TIME ZONE,
    last_modified TIMESTAMP(6) WITHOUT TIME ZONE,
    role VARCHAR(50)
);