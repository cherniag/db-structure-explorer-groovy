SET AUTOCOMMIT=0;
START TRANSACTION;

SET @communityId = (SELECT id FROM tb_communities WHERE name='mtv1');
SET @maxOrder = 10 + (SELECT MAX(payment_order) FROM tb_paymentPolicy WHERE communityID=@communityID AND paymentType='iTunesSubscription');

-- add new payment policies
insert into tb_paymentPolicy
( communityID, subWeeks, subCost,          paymentType, payment_policy_type, shortCode, currencyIso,                             app_store_product_id, advanced_payment_seconds, after_next_sub_payment_seconds, online, duration, duration_unit, payment_order) VALUES
(@communityId,        0,    1.49, 'iTunesSubscription',         'RECURRENT',        '',       'GBP',                 'com.musicqubed.ios.mtv1.payg.1',                        0,                              0,   true,      120,      'MONTHS',            10);

COMMIT;
SET AUTOCOMMIT = 1;
