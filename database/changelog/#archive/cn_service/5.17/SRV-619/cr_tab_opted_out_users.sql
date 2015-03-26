CREATE TABLE opted_out_users (
  id int AUTO_INCREMENT PRIMARY KEY,
  email varchar(100) CHARACTER SET utf8 NOT NULL,
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE opted_out_users ADD UNIQUE INDEX uq_opted_out_users_email (email);
