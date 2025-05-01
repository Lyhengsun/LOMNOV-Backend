CREATE DATABASE lumnov_db;

CREATE Table roles
(
    role_id SERIAL PRIMARY KEY,
    name    VARCHAR(50) NOT NULL
);

CREATE TABLE app_users
(
    app_user_id       SERIAL PRIMARY KEY,
    full_name         VARCHAR(50) NOT NULL,
    gender            VARCHAR(6)  NOT NULL,
    date_of_birth     DATE        NOT NULL,
    occupation        VARCHAR(50),
    phone_number      VARCHAR(15) NOT NULL,
    email             VARCHAR(50) NOT NULL UNIQUE,
    password          TEXT NOT NULL,
    is_verified       BOOLEAN DEFAULT FALSE,
    avatar_url        TEXT,
    emergency_contact VARCHAR(15),
    device_token      TEXT,
    role_id           INT         NOT NULL,
    CONSTRAINT fk_role_id FOREIGN KEY (role_id) REFERENCES roles (role_id) ON UPDATE CASCADE
);

-- OTP table
CREATE TABLE user_verifications
(
    id            serial PRIMARY KEY,
    expiry_date_time   timestamp,
    verified_code varchar(255),
    user_id       integer,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES app_users (app_user_id) ON DELETE CASCADE
);

drop table user_verifications;