SET AUTOCOMMIT=0;
START TRANSACTION;

SET @communityId = (SELECT id FROM tb_communities WHERE name='mtv1');
SET @maxOrder = 10 + (SELECT MAX(payment_order) FROM tb_paymentPolicy WHERE communityID=@communityID AND paymentType='iTunesSubscription');

-- add new payment policies
insert into tb_paymentPolicy
(      start_date_time,         end_date_time, communityID, subWeeks,  subCost,          paymentType, payment_policy_type, shortCode, currencyIso,                             app_store_product_id, advanced_payment_seconds, after_next_sub_payment_seconds, online, duration, duration_unit, payment_order) VALUES
('2015-05-18 00:00:01', '9999-12-31 23:59:59', @communityId,        0,    0.99, 'iTunesSubscription',           'ONETIME',        '',       'GBP',                 'com.musicqubed.ios.mtv1.payg.2',                        0,                              0,   true,       30,        'DAYS',     @maxOrder);

COMMIT;
SET AUTOCOMMIT = 1;
