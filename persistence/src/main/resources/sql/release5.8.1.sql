insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "5.8.1", "5.8.1");

-- SRV-268
select @mtv1_community_id:= c.id from tb_communities c where c.name = 'mtv1';

update tb_paymentPolicy
set app_store_product_id = 'com.musicqubed.ios.mtv1.subscription.monthly.0'
where communityID = @mtv1_community_id and paymentType='iTunesSubscription';

-- SRV-268

commit;
