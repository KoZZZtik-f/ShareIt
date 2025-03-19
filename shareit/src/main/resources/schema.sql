CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    available   BIT          NOT NULL,
    description VARCHAR(200) NULL,
    name        VARCHAR(30) NULL,
    );
