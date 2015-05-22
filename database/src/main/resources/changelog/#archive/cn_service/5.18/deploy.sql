use cn_service;

--SRV-644
ALTER TABLE tb_paymentPolicy ADD COLUMN payment_order INT NOT NULL DEFAULT 0;

--SRV-633
SET AUTOCOMMIT=0;
START TRANSACTION;

SET @communityId = (SELECT id FROM tb_communities WHERE name='mtv1');

-- turn off old payment policies
update tb_paymentPolicy set online = 0 where communityID = @communityId;

-- add new payment policies
insert into tb_paymentPolicy
( communityID, subWeeks, subCost,          paymentType, payment_policy_type, shortCode, currencyIso,                             app_store_product_id, advanced_payment_seconds, after_next_sub_payment_seconds, online, duration, duration_unit, payment_order) VALUES
(@communityId,        0,    4.49,            'PAY_PAL',         'RECURRENT',        '',       'GBP',                                             NULL,                        0,                              0,      1,        1,      'MONTHS',            10),
(@communityId,        0,    1.49,            'PAY_PAL',           'ONETIME',        '',       'GBP',                                             NULL,                        0,                              0,      1,        7,        'DAYS',            20),
(@communityId,        0,    4.99, 'iTunesSubscription',         'RECURRENT',        '',       'GBP', 'com.musicqubed.ios.mtv1.subscription.monthly.1',                        0,                              0,      1,        1,      'MONTHS',            10),
(@communityId,        0,    1.49, 'iTunesSubscription',           'ONETIME',        '',       'GBP',              'com.musicqubed.ios.mtv1.onetime.3',                        0,                              0,      1,        7,        'DAYS',            20);

COMMIT;
SET AUTOCOMMIT = 1;

--SRV-648
ALTER TABLE tb_paymentDetails
  ADD COLUMN token VARCHAR(40) DEFAULT NULL AFTER billingAgreementTxId,
  ADD COLUMN payerId VARCHAR(30) DEFAULT NULL AFTER token;

--SRV-664
alter table tb_promotions
  add column duration int not null default 0 after maxUsers,
  add column duration_unit VARCHAR(255) not null default 'WEEKS' after duration;

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

update tb_promotions set duration = freeWeeks, duration_unit = 'WEEKS';

--SRV-707
insert into system (release_time_millis, version, release_name) values(unix_timestamp(), "5.18", "5.18");
