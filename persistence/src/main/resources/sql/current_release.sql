-- SRV-388
USE cn_service;

-- Creating new table which will conflate data from two previous tables:
-- tb_userAndroidDetails and tb_useriPhoneDetails.
DROP TABLE IF EXISTS urban_airship_token;
CREATE TABLE urban_airship_token(
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id int(10) unsigned NOT NULL,
  token varchar(255) NOT NULL,
  CONSTRAINT pk_urban_airship_token PRIMARY KEY (id),
  CONSTRAINT fk_urban_airship_token_users FOREIGN KEY (user_id) REFERENCES tb_users(i),
  CONSTRAINT un_urban_airship_token_user_id UNIQUE (user_id)
);

START TRANSACTION;

-- Creating two temporary tables for interim data. Index is needed to speed up the
-- join of these tables on user_id column.
create temporary table users_on_android_1 (
  id int(10) unsigned NOT NULL,
  user_id int(10) unsigned NOT NULL,
  token varchar(255)
);
create index index_user_id_1 on users_on_android_1 (user_id);

create temporary table users_on_android_2 (
  id int(10) unsigned NOT NULL,
  user_id int(10) unsigned NOT NULL,
  token varchar(255)
);
create index index_user_id_2 on users_on_android_2 (user_id);

-- Selecting all users that belong to active communities and
-- moving all of them to temporary table 1.
insert into users_on_android_1(id, user_id, token)
    select ad.i as id, u.i as user_id, ad.token from tb_users as u
      inner join tb_userGroups as ug on u.userGroup = ug.id
      inner join tb_communities as c on ug.community = c.id
      inner join tb_deviceTypes as dt on u.deviceType = dt.i
      inner join tb_userAndroidDetails as ad on u.i = ad.userUID
    where c.name in ('o2','vf_nz', 'hl_uk', 'demo', 'demo3', 'demo2', 'demo4', 'demo5', 'demo6', 'mtv1', 'mtvnz')
          and ad.token is not null
          and ad.token != '(null)'
          and ad.token != ''
          and dt.name = 'ANDROID'
          and u.i not in (select user_id from urban_airship_token);

-- Copying all previously selected users to temporary table 2.
insert into users_on_android_2(id, user_id, token)
    select * from users_on_android_1;

-- At this moment we have 2 temporary tables with the same users. Thus
-- we can join them to receive unique users set without duplicate records for user_id and
-- last token value per group.
create temporary table users_on_android
    select t1.id, t1.user_id, t1.token from users_on_android_1 AS t1
      left join users_on_android_2 as t2
        on (t1.user_id = t2.user_id and t1.id < t2.id)
    where t2.id is null;

-- Moving all users to new table.
insert into urban_airship_token (user_id, token)
  select user_id, token from users_on_android;

-- Android tokens are migrated...

-- Creating two temporary tables for interim data. Index is needed to speed up the
-- join of these tables on user_id column.
create temporary table users_on_ios_1 (
  id int(10) unsigned NOT NULL,
  user_id int(10) unsigned NOT NULL,
  token varchar(255)
);
create index index_user_id_1 on users_on_ios_1 (user_id);

create temporary table users_on_ios_2 (
  id int(10) unsigned NOT NULL,
  user_id int(10) unsigned NOT NULL,
  token varchar(255)
);
create index index_user_id_2 on users_on_ios_2 (user_id);

-- Selecting all users that belong to active communities and
-- moving all of them to temporary table 1.
insert into users_on_ios_1(id, user_id, token)
    select iosd.i as id, u.i as user_id, iosd.token from tb_users as u
      inner join tb_userGroups as ug on u.userGroup = ug.id
      inner join tb_communities as c on ug.community = c.id
      inner join tb_deviceTypes as dt on u.deviceType = dt.i
      inner join tb_useriPhoneDetails as iosd on u.i = iosd.userUID
    where c.name in ('o2','vf_nz', 'hl_uk', 'demo', 'demo3', 'demo2', 'demo4', 'demo5', 'demo6', 'mtv1', 'mtvnz')
          and iosd.token is not null
          and iosd.token != '(null)'
          and iosd.token != ''
          and dt.name = 'IOS'
          and u.i not in (select user_id from urban_airship_token);

-- Copying all previously selected users to temporary table 2.
insert into users_on_ios_2(id, user_id, token)
    select * from users_on_ios_1;

-- At this moment we have 2 temporary tables with the same users. Thus
-- we can join them to receive unique users set without duplicate records for user_id and
-- last token value per group.
create temporary table users_on_ios
    select t1.id, t1.user_id, t1.token from users_on_ios_1 AS t1
      left join users_on_ios_2 as t2
        on (t1.user_id = t2.user_id and t1.id < t2.id)
    where t2.id is null;

-- Moving all users to new table.
insert into urban_airship_token (user_id, token)
  select user_id, token from users_on_ios;

-- IOS tokens are migrated...

drop temporary table users_on_android_1;
drop temporary table users_on_android_2;
drop temporary table users_on_android;

drop temporary table users_on_ios_1;
drop temporary table users_on_ios_2;
drop temporary table users_on_ios;

COMMIT;


-- START  http://jira.musicqubed.com/browse/SRV-560: [SERVER] Set up payment options for mtv1 community.
SET AUTOCOMMIT=0;
START TRANSACTION;

SET @communityId = (SELECT id FROM tb_communities WHERE name='mtv1');

UPDATE tb_paymentPolicy set online=0 where communityID=@communityID;
INSERT INTO tb_paymentPolicy
(communityID,  subWeeks, subCost, paymentType,          payment_policy_type, shortCode, currencyIso, app_store_product_id,                                advanced_payment_seconds, after_next_sub_payment_seconds, online, duration, duration_unit) VALUES
(@communityId, 0,        0.79,    'PAY_PAL',            'RECURRENT',         '',        'GBP',       null,                                                0,                        0,                              1,      1,        'WEEKS'),
(@communityId, 0,        7.99,    'PAY_PAL',            'ONETIME',           '',        'GBP',       null,                                                0,                        0,                              1,      3,        'MONTHS'),
(@communityId, 0,        12.99,   'PAY_PAL',            'ONETIME',           '',        'GBP',       null,                                                0,                        0,                              1,      6,        'MONTHS'),
(@communityId, 0,        0.79,    'iTunesSubscription', 'RECURRENT',         '',        'GBP',       'com.musicqubed.ios.mtv1.subscription.weekly.1',     0,                        0,                              1,      1,        'WEEKS'),
(@communityId, 0,        7.99,    'iTunesSubscription', 'ONETIME',           '',        'GBP',       'com.musicqubed.ios.mtv1.onetime.1'            ,     0,                        0,                              1,      3,        'MONTHS'),
(@communityId, 0,        12.99,   'iTunesSubscription', 'ONETIME',           '',        'GBP',       'com.musicqubed.ios.mtv1.onetime.2'            ,     0,                        0,                              1,      6,        'MONTHS');

COMMIT;
SET AUTOCOMMIT = 1;
-- END http://jira.musicqubed.com/browse/SRV-560: [SERVER] Set up payment options for mtv1 community.

-- SRV-590
set AUTOCOMMIT=0;
START TRANSACTION;

select @userGroupId:= c.id from tb_communities c join tb_userGroups ug on ug.community = c.id where c.name = 'mtvnz';

update tb_promotions p join tb_promoCode pC on p.i=pC.promotionId set isActive=false where userGroup=@userGroupId and pC.code='mtvnz.2weeks.promo.audio';

INSERT INTO tb_promotions
(description               , numUsers, maxUsers, startDate                            , endDate                              , isActive, freeWeeks, subWeeks, userGroup   , type       , showPromotion, label                     , is_white_listed) VALUES
('mtvnz.4weeks.promo.audio', 0       , 0       , UNIX_TIMESTAMP('2015-03-03 11:00:00'), UNIX_TIMESTAMP('2020-12-30 11:00:00'), TRUE    , 4        , 0       , @userGroupId, 'PromoCode', 0            , 'mtvnz.4weeks.promo.audio', FALSE);

INSERT INTO tb_promoCode
(code         , promotionId, media_type)
select p.label, i          , 'AUDIO' from tb_promotions p where p.userGroup=@userGroupId and p.label in ('mtvnz.4weeks.promo.audio');

commit;

SET AUTOCOMMIT = 1;
-- End of SRV-590