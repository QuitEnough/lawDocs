CREATE TABLE file_history.revinfo (
    rev BIGSERIAL PRIMARY KEY,
    revtmstmp BIGINT NOT NULL
);

CREATE TABLE file_history.directories_history (
    id BIGINT NOT NULL,
    rev BIGINT NOT NULL REFERENCES file_history.revinfo(rev),
    revtype SMALLINT NOT NULL,
    name VARCHAR(255),
    user_id BIGINT,
    parent_id BIGINT,
    PRIMARY KEY (id, rev)
);
CREATE INDEX idx_directories_history_rev ON file_history.directories_history (rev);

CREATE TABLE file_history.files_history (
    id BIGINT NOT NULL,
    rev BIGINT NOT NULL REFERENCES file_history.revinfo(rev),
    revtype SMALLINT NOT NULL,
    name VARCHAR(255),
    uuid UUID,
    directory BIGINT,
    user_id BIGINT,
    PRIMARY KEY (id, rev)
);
CREATE INDEX idx_files_history_rev ON file_history.files_history (rev);