INSERT INTO `cn_service`.`tb_paymentPolicy`      (`communityID`,         `subWeeks`,`subCost`,`paymentType`,`operator`,`shortCode`,`currencyIso`,`availableInStore`,`app_store_product_id`,`contract`,`segment`,`content_category`,`content_type`,`content_description`,`sub_merchant_id`,`provider`,`tariff`,`media_type`,`is_default`,`advanced_payment_seconds`,`after_next_sub_payment_seconds`,`online`) VALUES
((select id from tb_communities where rewriteURLParameter = 'hl_uk'),         1,     1.49,    'PAY_PAL',      NULL,         '',        'GBP',                 1,                  NULL,      NULL,     NULL,              NULL,          NULL,                 NULL,             NULL,    'FACEBOOK',   '_3G',     'AUDIO',           0,                        0,                               0,       1);