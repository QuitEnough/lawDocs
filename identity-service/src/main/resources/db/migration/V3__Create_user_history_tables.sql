CREATE TABLE user_history.revinfo (
    rev BIGSERIAL PRIMARY KEY,
    revtmstmp BIGINT NOT NULL
);

CREATE TABLE user_history.users_history (
    id BIGINT NOT NULL,
    rev BIGINT NOT NULL REFERENCES user_history.revinfo(rev),
    revtype SMALLINT NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    telegram_id VARCHAR(255),
    email VARCHAR(200),
    password VARCHAR(200),
    create_date TIMESTAMP(6),
    last_modified TIMESTAMP(6),
    role VARCHAR(50),
    PRIMARY KEY (id, rev)
);
CREATE INDEX idx_users_history_rev ON user_history.users_history (rev);