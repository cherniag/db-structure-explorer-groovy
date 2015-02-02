-- SRV-425
use cn_service;

alter table tb_paymentPolicy add column payment_policy_type varchar(255) not null default 'RECURRENT' comment 'enum values are RECURRENT and ONETIME' after paymentType;
alter table tb_submittedPayments add column payment_policy_id int(11) comment 'reference to payment policy being used' after paymentDetailsId;
-- end of SRV-425

-- START OF COMMENTED CODE: FOR FUTURE RELEASES (OR THIS RELEASE AS SEPARATE TASK TO ADD NEW PAYMENT POLICY
-- set autocommit = 0;
-- start transaction;

-- set @community_id = (select id from tb_communities where rewriteurlparameter = 'mtv1');
-- insert into tb_paymentPolicy (communityID, 	subWeeks, subCost, paymentType,          operator, shortCode, currencyIso, availableInStore, app_store_product_id,                contract, segment, content_category, content_type, content_description, sub_merchant_id, provider, tariff, media_type, advanced_payment_seconds, after_next_sub_payment_seconds, is_default, online, payment_policy_type, duration, duration_unit)
--                      values (@community_id,       0,    1.49, 'iTunesSubscription',     NULL,        '',       'GBP',            FALSE, 'com.musicqubed.ios.mtv1.onetime.0',     NULL,    NULL,             NULL,         NULL,                NULL,            NULL,     null,  '_3G',    'AUDIO',                        0,                              0,      FALSE,   TRUE,           'ONETIME',        7,        'DAYS');

-- commit;
-- set autocommit = 1;
-- END OF COMMENTED CODE