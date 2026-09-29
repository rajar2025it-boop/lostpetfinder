CREATE DATABASE IF NOT EXISTS lostpetfinder;
USE lostpetfinder;

CREATE TABLE users (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(100) NOT NULL,
  email       VARCHAR(150) NOT NULL UNIQUE,
  phone       VARCHAR(15)  NOT NULL,
  created_at  DATETIME     NOT NULL
);

CREATE TABLE lost_pet_reports (
  id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id             BIGINT       NOT NULL,
  pet_name            VARCHAR(80),
  species             VARCHAR(20)  NOT NULL,   -- DOG | CAT | BIRD | RABBIT | OTHER
  breed               VARCHAR(80),
  colour              VARCHAR(50)  NOT NULL,
  locality            VARCHAR(100) NOT NULL,
  last_seen_location  VARCHAR(200) NOT NULL,
  last_seen_date      DATE         NOT NULL,
  description         VARCHAR(500),
  status              VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE | RESOLVED
  created_at          DATETIME     NOT NULL,
  resolved_at         DATETIME,
  CONSTRAINT fk_lost_user FOREIGN KEY (user_id) REFERENCES users(id),
  INDEX idx_lost_match (species, locality, status)
);

CREATE TABLE found_animal_reports (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id         BIGINT       NOT NULL,
  species         VARCHAR(20)  NOT NULL,
  breed           VARCHAR(80),
  colour          VARCHAR(50)  NOT NULL,
  locality        VARCHAR(100) NOT NULL,
  found_location  VARCHAR(200) NOT NULL,
  found_date      DATE         NOT NULL,
  description     VARCHAR(500),
  status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
  created_at      DATETIME     NOT NULL,
  resolved_at     DATETIME,
  CONSTRAINT fk_found_user FOREIGN KEY (user_id) REFERENCES users(id),
  INDEX idx_found_match (species, locality, status)
);
