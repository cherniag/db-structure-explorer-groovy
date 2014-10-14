-- begin SRV-291
SET autocommit = 0;
START TRANSACTION;

select @mtv1_community_id:= c.id from tb_communities c where c.name = 'mtv1';

update tb_paymentPolicy
set subCost = 0.99, subWeeks = 1
where communityID = @mtv1_community_id and paymentType in ('iTunesSubscription', 'PAY_PAL');

update tb_paymentPolicy
set app_store_product_id = 'com.musicqubed.ios.mtv1.subscription.weekly.0'
where communityID = @mtv1_community_id and paymentType='iTunesSubscription';

COMMIT;
-- end SRV-291