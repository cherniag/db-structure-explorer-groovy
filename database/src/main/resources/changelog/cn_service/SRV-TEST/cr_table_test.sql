-- liquibase formatted sql logicalFilePath:SRV-TEST_cr_table_test.sql
-- changeset enes:SRV-TEST_cr_table_test.sql-1

CREATE TABLE a_test_table (
  id int AUTO_INCREMENT PRIMARY KEY,
  email varchar(100) CHARACTER SET utf8 NOT NULL,
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);
