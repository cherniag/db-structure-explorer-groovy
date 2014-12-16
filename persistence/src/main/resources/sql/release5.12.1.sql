insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "5.12.1", "5.12.1");

-- SRV-423
create table itunes_payment_lock(
  id bigint PRIMARY KEY AUTO_INCREMENT,
  user_id int,
  next_sub_payment int,
  constraint `user_id-next_sub_payment` unique (user_id, next_sub_payment)
);

set autocommit = 0;
start transaction;

  create table `bkp_tb_submittedPayments_srv-423`
      select * from tb_submittedPayments sp
      where paymentSystem = 'iTunesSubscription' and
            sp.i <> (select min(i) from tb_submittedPayments where sp.userId=userId and sp.next_sub_payment = next_sub_payment and paymentSystem = 'iTunesSubscription');

  delete from tb_submittedPayments where i in (select i from `bkp_tb_submittedPayments_srv-423`);

  create table `bkp_tb_accountLog_srv-423`
      select * from tb_accountLog
      where relatedPaymentUID in (select i from `bkp_tb_submittedPayments_srv-423`) and transactionType = 3;

  delete from tb_accountLog where i in (select i from `bkp_tb_accountLog_srv-423`);

commit;
set autocommit = 1;
-- end of SRV-423