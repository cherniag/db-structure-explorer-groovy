-- Initial Size: 0 
-- Growth Rate: per /day/month/year
-- Retention Policy: weekly
-- Affects BI: no

use cn_service;

CREATE TABLE new_zealand_subscriber_info (
  id int UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id int(10) UNSIGNED NOT NULL,
  msisdn varchar(20) NOT NULL,
  pay_indicator varchar(60) NOT NULL,
  provider_name varchar(60) CHARACTER SET utf8 NOT NULL,
  billing_account_number varchar(60) NOT NULL,
  billing_account_name varchar(255) CHARACTER SET utf8,
  active bit NOT NULL DEFAULT 0,
  create_timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE new_zealand_subscriber_info ADD CONSTRAINT fk_nz_subscriber_info_users FOREIGN KEY (user_id) REFERENCES tb_users(i);
