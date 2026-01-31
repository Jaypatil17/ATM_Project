show databases;
use atm;
CREATE TABLE users (
    debit_card_no VARCHAR(16) PRIMARY KEY,
    pin VARCHAR(4),
    balance DOUBLE
);
INSERT INTO users VALUES ('9096545751', '1234', 10000);
INSERT INTO users VALUES ('8329210629', '1234', 20000);
INSERT INTO users VALUES ('0123456789', '1234', 30000);
