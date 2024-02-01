--liquibase formatted sql

--changeset barabanov:file
CREATE TABLE file
(
    id          BIGSERIAL       PRIMARY KEY,
    name        VARCHAR(128),
    md_5        CHAR(32),
    tracked     BOOLEAN
);

--changeset barabanov:user_file
CREATE TABLE user_file
(
    id          BIGSERIAL       PRIMARY KEY,
    file_id     BIGINT          REFERENCES file(id),
    user_id     BIGINT          REFERENCES users(id)
);