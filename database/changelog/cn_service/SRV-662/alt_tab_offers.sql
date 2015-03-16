alter table tb_pendingpayments drop column offerId;

alter table tb_submittedpayments drop column offerId;

alter table tb_accountlog drop FOREIGN KEY tb_accountLog_U_offerId;
alter table tb_accountlog drop INDEX tb_accountLog_PK_offerId;
alter table tb_accountlog drop column offerId;

drop table offers_filters;
drop table offer_items;
drop table offers;
