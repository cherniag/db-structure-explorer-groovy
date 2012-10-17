-- Execute this file only after release3.0.sql and migrationPaymentsIntoPaymentDetails.sql
alter table tb_paymentPolicy modify operator int(10) unsigned  DEFAULT NULL;
update tb_paymentPolicy set operator=NULL where operator=0;