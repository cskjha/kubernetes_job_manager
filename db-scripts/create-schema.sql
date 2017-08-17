--drop old user and schema if exists
DROP SCHEMA IF EXISTS lambda CASCADE;
DROP ROLE IF EXISTS lambda_admin;

--create admin user and schema
CREATE USER lambda_admin;
CREATE SCHEMA lambda;
ALTER USER lambda_admin WITH SUPERUSER;
ALTER USER lambda_admin WITH PASSWORD 'lambda_admin';
ALTER SCHEMA lambda OWNER TO lambda_admin;

--create tables
CREATE TABLE lambda.app
(
    name VARCHAR(255) PRIMARY KEY NOT NULL,
    configs TEXT
);

CREATE TABLE lambda.function
(
    name VARCHAR(255) PRIMARY KEY NOT NULL,
    image TEXT,
    app VARCHAR(255),
    configs TEXT,
    envs TEXT,
    timeout INTEGER,
    memory INTEGER,
    max_retry_count INTEGER,
    priority INTEGER,
    disabled BOOLEAN,
    CONSTRAINT fk_function_app FOREIGN KEY (app) REFERENCES lambda.app(name) ON DELETE CASCADE
);

CREATE TABLE lambda.event
(
    id VARCHAR(255) PRIMARY KEY NOT NULL,
    function VARCHAR(255),
    app VARCHAR(255),
    owner VARCHAR(255),
    status VARCHAR(255),
    valid_from BIGINT,
    last_updated BIGINT,
    payload TEXT,
    response_body TEXT,
    configs TEXT,
    version INTEGER,
    retry INTEGER,
    retryReason VARCHAR (255),
    priority INTEGER,
    CONSTRAINT fk_event_app FOREIGN KEY (app) REFERENCES lambda.app (name) ON DELETE CASCADE,
    CONSTRAINT fk_event_function FOREIGN KEY (function) REFERENCES lambda.function (name) ON DELETE CASCADE
);
