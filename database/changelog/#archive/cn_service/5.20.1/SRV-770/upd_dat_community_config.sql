SET AUTOCOMMIT = 0;
START TRANSACTION;

SET @old_community_name = 'mtvit';
SET @new_community_name = 'mtvie';

-- updates 1 records on prod
UPDATE tb_communities SET name=@new_community_name, displayName=@new_community_name, assetName=@new_community_name, rewriteURLParameter=@new_community_name WHERE name=@old_community_name;
-- updates 1 records on prod
UPDATE tb_news SET name=@new_community_name WHERE name=@old_community_name;
-- updates 1 records on prod
UPDATE tb_userGroups SET name=@new_community_name WHERE name=@old_community_name;
-- updates 1 records on prod
UPDATE tb_promotions SET description='mtviePromo2weeksAudio', label='mtvie.2weeks.promo.audio' WHERE description='mtvitPromo2weeksAudio';
-- updates 1 records on prod
UPDATE tb_promoCode  SET code='mtvie.2weeks.promo.audio' WHERE code='mtvit.2weeks.promo.audio';
-- updates 0 records on prod
UPDATE tb_paymentPolicy SET app_store_product_id='com.musicqubed.ios.mtvie.subscription.monthly.1' where app_store_product_id='com.musicqubed.ios.mtvit.subscription.monthly.1';

-- updates 8 records one by one on prod
UPDATE tb_charts SET name='HOT_TRACKS - MTVIE' WHERE name='HOT_TRACKS - MTVIT';
UPDATE tb_charts SET name='FIFTH_CHART - MTVIE' WHERE name='FIFTH_CHART - MTVIT';
UPDATE tb_charts SET name='PLAYLIST_1 - MTVIE' WHERE name='PLAYLIST_1 - MTVIT';
UPDATE tb_charts SET name='PLAYLIST_2 - MTVIE' WHERE name='PLAYLIST_2 - MTVIT';
UPDATE tb_charts SET name='PLAYLIST_3 - MTVIE' WHERE name='PLAYLIST_3 - MTVIT';
UPDATE tb_charts SET name='OTHER_CHART - MTVIE' WHERE name='OTHER_CHART - MTVIT';
UPDATE tb_charts SET name='PLAYLIST_4 - MTVIE' WHERE name='PLAYLIST_4 - MTVIT';
UPDATE tb_charts SET name='PLAYLIST_5 - MTVIE' WHERE name='PLAYLIST_5 - MTVIT';

-- updates 8 records one by one on prod
UPDATE tb_chartDetail SET image_title='HOT_TRACKS - MTVIE',   subtitle='HOT_TRACKS - MTVIE',  title='HOT_TRACKS - MTVIE'  WHERE title='HOT_TRACKS - MTVIT';
UPDATE tb_chartDetail SET image_title='FIFTH_CHART - MTVIE',  subtitle='FIFTH_CHART - MTVIE', title='FIFTH_CHART - MTVIE' WHERE title='FIFTH_CHART - MTVIT';
UPDATE tb_chartDetail SET image_title='PLAYLIST_1 - MTVIE',   subtitle='PLAYLIST_1 - MTVIE',  title='PLAYLIST_1 - MTVIE'  WHERE title='PLAYLIST_1 - MTVIT';
UPDATE tb_chartDetail SET image_title='PLAYLIST_2 - MTVIE',   subtitle='PLAYLIST_2 - MTVIE',  title='PLAYLIST_2 - MTVIE'  WHERE title='PLAYLIST_2 - MTVIT';
UPDATE tb_chartDetail SET image_title='PLAYLIST_3 - MTVIE',   subtitle='PLAYLIST_3 - MTVIE',  title='PLAYLIST_3 - MTVIE'  WHERE title='PLAYLIST_3 - MTVIT';
UPDATE tb_chartDetail SET image_title='OTHER_CHART - MTVIE',  subtitle='OTHER_CHART - MTVIE', title='OTHER_CHART - MTVIE' WHERE title='OTHER_CHART - MTVIT';
UPDATE tb_chartDetail SET image_title='PLAYLIST_4 - MTVIE',   subtitle='PLAYLIST_4 - MTVIE',  title='PLAYLIST_4 - MTVIE'  WHERE title='PLAYLIST_4 - MTVIT';
UPDATE tb_chartDetail SET image_title='PLAYLIST_5 - MTVIE',   subtitle='PLAYLIST_5 - MTVIE',  title='PLAYLIST_5 - MTVIE'  WHERE title='PLAYLIST_5 - MTVIT';

COMMIT;
SET AUTOCOMMIT = 1;