DROP TABLE IF EXISTS tb_paymentDetails;
CREATE TABLE tb_paymentDetails (
  paymentType varchar(31) NOT NULL,
  i bigint(20) NOT NULL AUTO_INCREMENT,
  creationTimestampMillis bigint(20) NOT NULL,
  descriptionError varchar(255) DEFAULT NULL,
  disableTimestampMillis bigint(20) NOT NULL,
  lastPaymentStatus varchar(255) DEFAULT NULL,
  madeRetries int(11) NOT NULL,
  VPSTxId varchar(255) DEFAULT NULL,
  released bit(1) DEFAULT NULL,
  securityKey varchar(255) DEFAULT NULL,
  txAuthNo varchar(255) DEFAULT NULL,
  vendorTxCode varchar(255) DEFAULT NULL,
  paymentPolicyId smallint(6) DEFAULT NULL,
  retriesOnError int(11) NOT NULL,
  billingAgreementTxId varchar(255) DEFAULT NULL,
  migPhoneNumber varchar(255) DEFAULT NULL,
  activated bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (i),
  KEY FK60BC1B4D10EFFD28 (paymentPolicyId)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS tb_pendingPayments;
CREATE TABLE tb_pendingPayments (
  i bigint(20) NOT NULL AUTO_INCREMENT,
  amount decimal(19,2) DEFAULT NULL,
  currencyISO varchar(255) DEFAULT NULL,
  internalTxId varchar(255) DEFAULT NULL,
  paymentSystem varchar(255) DEFAULT NULL,
  subweeks int(11) NOT NULL,
  timestamp int(11) NOT NULL,
  userId int(11) DEFAULT NULL,
  type varchar(255) DEFAULT NULL,
  PRIMARY KEY (i)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS tb_submittedPayments;
CREATE TABLE tb_submittedPayments (
  i bigint(20) NOT NULL AUTO_INCREMENT,
  amount decimal(19,2) DEFAULT NULL,
  currencyISO varchar(255) DEFAULT NULL,
  internalTxId varchar(255) DEFAULT NULL,
  paymentSystem varchar(255) DEFAULT NULL,
  subweeks int(11) NOT NULL,
  timestamp int(11) NOT NULL,
  status varchar(255) DEFAULT NULL,
  descriptionError varchar(255) DEFAULT NULL,
  userId int(11) DEFAULT NULL,
  type varchar(255) DEFAULT NULL,
  PRIMARY KEY (i)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8;

alter table tb_accountLog modify balanceAfter int(11) NOT NULL;
alter table tb_paymentPolicy add column currencyIso varchar(255) DEFAULT NULL;

alter table tb_users add column lastSuccessfulPaymentTimeMillis bigint(20) DEFAULT 0;
alter table tb_users modify subBalance int(11) NOT NULL;
alter table tb_users add column currentPaymentDetailsId bigint(20) DEFAULT NULL;
alter table tb_users modify paymentType varchar(255) DEFAULT 'UNKNOWN';
alter table tb_pendingPayments modify timestamp bigint(20) DEFAULT 0;
alter table tb_pendingPayments add column expireTimeMillis bigint(20) DEFAULT 0;
alter table tb_submittedPayments modify timestamp bigint(20) DEFAULT 0;
alter table tb_users add column facebookId varchar(45) after gender;

update tb_paymentPolicy set currencyISO='GBP' where currencyISO is NULL;
update tb_drmPolicy set drmType=1 where drmType=0;

-- CL-3793: Implement a batch to send notifications
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-3793

-- create table tb_useriPhoneDetails(
--        `i` INT not null auto_increment,
--       `userUID` INT,
--       `token` VARCHAR(64),
--       `usergroup` INT,
--       `nbUpdates` INT,
--       `status` INT,
--        primary key (`i`)
-- );

create unique index `primary_key` on `cn_service`.`tb_useriPhoneDetails`(`i`);
alter table tb_useriPhoneDetails modify token VARCHAR(64);