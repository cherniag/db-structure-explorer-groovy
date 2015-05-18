SET @communityId = (SELECT id FROM tb_communities WHERE name='mtvit');

-- add new payment policies
insert into tb_paymentPolicy
( communityID, subWeeks, subCost,          paymentType, payment_policy_type, shortCode, currencyIso,                              app_store_product_id, advanced_payment_seconds,   after_next_sub_payment_seconds, online, duration, duration_unit, payment_order) VALUES
(@communityId,        0,    1.00,            'PAY_PAL',         'RECURRENT',        '',       'GBP',                                              NULL,                        0,                                0,      1,        1,      'MONTHS',            10),
(@communityId,        0,    1.00, 'iTunesSubscription',         'RECURRENT',        '',       'GBP', 'com.musicqubed.ios.mtvit.subscription.monthly.1',                        0,                                0,      1,        1,      'MONTHS',            10);
