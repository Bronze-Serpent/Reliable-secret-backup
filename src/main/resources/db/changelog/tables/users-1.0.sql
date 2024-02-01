--liquibase formatted sql

--changeset barabanov:users
CREATE TABLE users(
      id        BIGSERIAL       PRIMARY KEY,
      username  VARCHAR(64)     NOT NULL    UNIQUE,
      password  VARCHAR(128)
);