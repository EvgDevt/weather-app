CREATE TABLE IF NOT EXISTS users
(
    id          SERIAL PRIMARY KEY,
    email       VARCHAR(32) NOT NULL UNIQUE,
    password    VARCHAR(128) NOT NULL,
    firstname   VARCHAR(32),
    lastname    VARCHAR(32),
    role        VARCHAR(16) NOT NULL,
    created_at  TIMESTAMP,
    modified_at TIMESTAMP,
    modified_by VARCHAR(32),
    created_by  VARCHAR(32) DEFAULT 'SYSTEM'::character varying NOT NULL
    );