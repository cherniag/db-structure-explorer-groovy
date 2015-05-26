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
