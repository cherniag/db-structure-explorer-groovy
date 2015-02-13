-- Initial Size: 0
-- Growth Rate: per /day/month/year
-- Retention Policy: weekly
-- Affects BI: no

use cn_service;

CREATE TABLE pin_code (
  id int UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id int(10) UNSIGNED NOT NULL,
  code varchar(50) NOT NULL,
  attempts smallint NOT NULL,
  entered bit NOT NULL DEFAULT 0,
  creation_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE pin_code ADD CONSTRAINT fk_pin_code_users FOREIGN KEY (user_id) REFERENCES tb_users(i);