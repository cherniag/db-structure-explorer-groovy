CREATE TABLE user_transactions(
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  create_timestamp BIGINT(20) NOT NULL,
  user_id INT(10) UNSIGNED NOT NULL,
  start_timestamp BIGINT(20) NOT NULL,
  end_timestamp BIGINT(20),
  transaction_type VARCHAR(50) NOT NULL,
  promo_code VARCHAR(255),
  CONSTRAINT `fk_user_transactions_tb_users` FOREIGN KEY (user_id) REFERENCES tb_users (i)
);
