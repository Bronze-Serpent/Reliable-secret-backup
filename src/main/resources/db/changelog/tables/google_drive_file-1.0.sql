--liquibase formatted sql

--changeset barabanov:google_drive_file
CREATE TABLE google_drive_file
(
    id          BIGSERIAL   PRIMARY KEY,
    file_id     BIGINT      NOT NULL    REFERENCES file(id),
    google_id   BIGINT      NOT NULL    UNIQUE
);