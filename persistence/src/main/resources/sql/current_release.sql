-- begin SRV-294
set AUTOCOMMIT=0;
START TRANSACTION;
select @mtv1_community_id:= c.id from tb_communities c where c.name = 'mtv1';
update tb_paymentPolicy
set subcost = 4.99, duration_unit = 'MONTHS', duration=1
where communityID = @mtv1_community_id and paymentType in ('iTunesSubscription', 'PAY_PAL');

update tb_paymentPolicy
set app_store_product_id = 'com.musicqubed.ios.mtv1.subscription.monthly.0'
where communityID = @mtv1_community_id and paymentType='iTunesSubscription';
COMMIT;
--  end SRV-294