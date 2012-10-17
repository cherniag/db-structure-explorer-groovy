alter table tb_pendingPayments add column externalTxId varchar(255) NOT NULL;
alter table tb_submittedPayments add column externalTxId varchar(255) NOT NULL;