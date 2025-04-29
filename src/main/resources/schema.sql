CREATE DATABASE lumnov_db;

CREATE Table roles (
  role_id SERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL
);

CREATE TABLE app_users (
  full_name VARCHAR(50) NOT NULL,
  gender VARCHAR(6) NOT NULL,
  date_of_birth DATE NOT NULL,
  occupation VARCHAR(50),
  phone_number VARCHAR(15) NOT NULL,
  is_verified BOOLEAN DEFAULT false,
  avatar_url TEXT,
  emergency_contact VARCHAR(15),
  device_token TEXT,
  role_id INT NOT NULL,
  CONSTRAINT fk_role_id FOREIGN KEY (role_id) REFERENCES roles(role_id) ON UPDATE CASCADE
);