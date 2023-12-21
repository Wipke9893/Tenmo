BEGIN TRANSACTION;

DROP TABLE IF EXISTS tenmo_user;
DROP SEQUENCE IF EXISTS seq_user_id;

CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  START WITH 1001
  NO MAXVALUE;

CREATE TABLE tenmo_user (
	user_id int NOT NULL DEFAULT nextval('seq_user_id'),
	username varchar(50) UNIQUE NOT NULL,
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


INSERT INTO tenmo_user (username,password_hash,role) VALUES ('user1','user1','ROLE_USER'); -- 1001
INSERT INTO tenmo_user (username,password_hash,role) VALUES ('user2','user2','ROLE_USER'); -- 1002
INSERT INTO tenmo_user (username,password_hash,role) VALUES ('user3','user3','ROLE_USER');
INSERT INTO accounts (user_id) VALUES (1001);
INSERT INTO accounts (user_id) VALUES (1002);
INSERT INTO transfers (pending, denied, amount_transferred, sender_id, receiver_id, date_time) VALUES (true, false, 100, 1, 2, '2020-01-01 00:00:00');
INSERT INTO transfers (pending, denied, amount_transferred, sender_id, receiver_id, date_time) values (true, false, 100, 2, 1, '2020-01-01 00:00:00');

COMMIT TRANSACTION;


