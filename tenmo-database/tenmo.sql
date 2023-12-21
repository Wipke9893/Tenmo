rollback;
BEGIN TRANSACTION;
-- Drop tables and sequence
DROP TABLE IF EXISTS transfers;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS tenmo_user;
DROP SEQUENCE IF EXISTS seq_user_id;
-- Create sequence for user_id
CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  START WITH 1001
  NO MAXVALUE;
-- Create tenmo_user table
CREATE TABLE tenmo_user (
	user_id int NOT NULL DEFAULT nextval('seq_user_id'),
	username varchar(50) NOT NULL,
	password_hash varchar(200) NOT NULL,
	role varchar(20),
	CONSTRAINT PK_tenmo_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username)
);
CREATE TABLE accounts (
account_id SERIAL PRIMARY KEY,
balance DECIMAL(12,2) DEFAULT 1000,
user_id int NOT NULL,
	
	FOREIGN KEY (user_id) REFERENCES tenmo_user(user_id)
);
-- Create transfers table
CREATE TABLE transfers (
	transfer_id SERIAL PRIMARY KEY,
	pending boolean DEFAULT true,
	denied boolean DEFAULT false,
	amount_transferred decimal(10,2) NOT NULL,
	sender_id int NOT NULL,
	receiver_id int NOT NULL,
	date_time timestamp DEFAULT current_timestamp,
	
	FOREIGN KEY (sender_id) REFERENCES accounts(account_id),
    FOREIGN KEY (receiver_id) REFERENCES accounts(account_id)
);
COMMIT;