alter table tb_pendingPayments drop column offerId;

alter table tb_submittedPayments drop column offerId;

alter table tb_accountLog drop FOREIGN KEY tb_accountLog_U_offerId;
alter table tb_accountLog drop INDEX tb_accountLog_PK_offerId;
alter table tb_accountLog drop column offerId;

drop table offers_filters;
drop table offer_items;
drop table offers;
