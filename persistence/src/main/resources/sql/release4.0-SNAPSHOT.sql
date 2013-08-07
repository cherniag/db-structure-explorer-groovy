 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "4.0-SN", "4.0-SN");

 -- IMP-1776 [Server] Update the Get Chart command to include Video
alter table tb_files add column duration int unsigned not null;

insert into tb_charts (name, numTracks, genre, timestamp, numBonusTracks, type)
select
'Video Chart',
ch.numTracks,
ch.genre,
ch.timestamp,
ch.numBonusTracks,
'VIDEO_CHART'
from tb_charts ch
join community_charts cc on cc.chart_id = ch.i
join tb_communities c on cc.community_id = c.i and c.rewriteURLParameter = 'o2'
where ch.type='BASIC_CHART';

insert into community_charts (chart_id, community_id)
select
ch.i,
c.i
from tb_charts ch
join tb_communities c on c.rewriteURLParameter = 'o2'
where ch.type = 'VIDEO_CHART';

insert into tb_chartDetail (subtitle, chart, position, publishTimeMillis, version, title)
select
'Video Chart',
ch.i,
8,
unix_timestamp('2013-01-01')*1000,
0,
ch.name
from tb_charts ch
join community_charts cc on cc.chart_id = ch.i
join tb_communities c on cc.community_id = c.i and c.rewriteURLParameter = 'o2'
where ch.type='VIDEO_CHART';

 -- http://jira.musicqubed.com/browse/IMP-1784
 -- [Server] Adjust payment system and jobs to support new 4G payment options
alter table tb_paymentPolicy add column tariff char(255) not null default '_3G';
alter table tb_paymentPolicy add column media_type char(255) not null default 'AUDIO';

-- IMP-1774 [Server] Update the Account Check command to include the Video access flags
alter table tb_users add column tariff char(255) not null default '_3G';
alter table tb_users add column video_free_trial_has_been_activated boolean not null default false;
alter table tb_promoCode add column media_type char(255) not null default 'AUDIO';
alter table tb_users drop column paymentEnabled;

 -- http://jira.musicqubed.com/browse/IMP-1782
 -- [Server] Calculate and store the Refund when user activates Video
create table refund (
  id bigint not null auto_increment,
  log_time_millis bigint,
  next_sub_payment_millis bigint,
  payment_details_id bigint(20) not null,
  user_id int(10) unsigned not null,
  primary key (id))
 engine=INNODB DEFAULT CHARSET=utf8;

 alter table refund add index refund_PK_payment_details_id (payment_details_id), add constraint refund_U_payment_details_id foreign key (payment_details_id) references tb_paymentDetails (i);
 alter table refund add index refund_PK_user_id (user_id), add constraint refund_U_user_id foreign key (user_id) references tb_users (i);

 alter table tb_users add column last_successful_payment_details_id bigint(20);
 alter table tb_users add index tb_users_PK_last_successful_payment_details (last_successful_payment_details_id), add constraint tb_users_U_last_successful_payment_details foreign key (last_successful_payment_details_id) references tb_paymentDetails (i);

 -- IMP-1785: [Server] Add new promotion types for 4G users
 ALTER TABLE tb_promotions MODIFY COLUMN description char(100) NOT NULL;
 ALTER TABLE tb_promotions MODIFY COLUMN label varchar(50);

 ALTER TABLE tb_users DROP FOREIGN KEY FKFAEDF4F753C7738F;
 ALTER TABLE tb_promocode DROP FOREIGN KEY FKF393230B3BEC8C9E;

 ALTER TABLE tb_users MODIFY COLUMN potentialPromotion_i INT;
 ALTER TABLE tb_promotions_tb_filter MODIFY COLUMN tb_promotions_i INT NOT NULL;
 ALTER TABLE tb_promotionpaymentpolicy MODIFY COLUMN promotion_i INT;
 ALTER TABLE tb_promotions MODIFY COLUMN i INT NOT NULL AUTO_INCREMENT;
 ALTER TABLE tb_promocode MODIFY COLUMN promotionId INT;
 ALTER TABLE tb_users MODIFY COLUMN potentialPromoCodePromotion_i INT;

 alter table tb_users add index tb_users_PK_potentialPromoCodePromotion_i (potentialPromoCodePromotion_i), add constraint tb_users_U_potentialPromoCodePromotion_i foreign key (potentialPromoCodePromotion_i) references tb_promotions (i);
 alter table tb_promocode add index tb_promocode_PK_promotionId (promotionId), add constraint tb_promocode_U_promotionId foreign key (promotionId) references tb_promotions (i);

 insert into tb_promotions(description, startDate, endDate, isActive, freeWeeks, userGroup, type, label, numUsers, maxUsers, subWeeks, showPromotion)
   value('o2 Video Audio Free Trial for 4G PAYM direct consumers before 2014', unix_timestamp(), 1388527200, true, 52, 10, 'PromoCode', 'o2.consumer.4g.paym.direct.till.end.of.2013', 0, 0, 0, false);

 insert into tb_promotions(description, startDate, endDate, isActive, freeWeeks, userGroup, type, label, numUsers, maxUsers, subWeeks, showPromotion)
   value('o2 Video Audio Free Trial for 4G PAYM direct consumers after 2013', 1388527200, 2147483647, true, 8, 10, 'PromoCode', 'o2.consumer.4g.paym.direct.after.end.of.2013', 0, 0, 0, false);

 insert into tb_promotions(description, startDate, endDate, isActive, freeWeeks, userGroup, type, label, numUsers, maxUsers, subWeeks, showPromotion)
   value('o2 Video Audio Free Trial for 4G PAYM indirect consumers', unix_timestamp(), 1388527200, true, 8, 10, 'PromoCode', 'o2.consumer.4g.paym.indirect', 0, 0, 0, false);

 insert into tb_promotions(description, startDate, endDate, isActive, freeWeeks, userGroup, type, label,  numUsers, maxUsers, subWeeks, showPromotion)
   value('o2 Video Audio Free Trial for 4G PAYG consumers', unix_timestamp(), 2147483647, true, 8, 10, 'PromoCode', 'o2.consumer.4g.payg', 0, 0, 0, false);

 insert into tb_promoCode(code, promotionId, media_type) select 'o2.consumer.4g.paym.direct', i, 'VIDEO_AND_AUDIO' from tb_promotions where label = 'o2.consumer.4g.paym.direct.till.end.of.2013';
 insert into tb_promoCode(code, promotionId, media_type) select 'o2.consumer.4g.paym.direct', i, 'VIDEO_AND_AUDIO' from tb_promotions where label = 'o2.consumer.4g.paym.direct.after.end.of.2013';
 insert into tb_promoCode(code, promotionId, media_type) select label, i, 'VIDEO_AND_AUDIO' from tb_promotions where label = 'o2.consumer.4g.paym.indirect';
 insert into tb_promoCode(code, promotionId, media_type) select label, i, 'VIDEO_AND_AUDIO' from tb_promotions where label = 'o2.consumer.4g.payg';

 --
 alter table tb_users add column contract_channel varchar(255) default 'DIRECT';

 -- http://jira.musicqubed.com/browse/IMP-1794
 -- Remove video access from downgrading users
 insert into tb_accountLogTypes (i, name) value (11, "Trial skipping");
 insert into tb_accountLogTypes (i, name) value (12, "Bought period skipping");

 -- IMP-1781 [Track Repo] Migrate tracks ingestion from CMS to Track Repo
 insert into tb_fileTypes (i, name) value (4, 'VIDEO');
 alter table tb_media modify column headerFile int unsigned null;
 alter table tb_media modify column audioPreviewFile int unsigned null;
 alter table tb_media modify column headerPreviewFile int unsigned null;
 alter table tb_media modify column purchasedFile int unsigned null;

 alter table tb_users add column last_promo int(10);
 ALTER TABLE tb_promoCode MODIFY COLUMN id INT NOT NULL AUTO_INCREMENT;
 alter table tb_users add constraint user_promo_code_fk foreign key (last_promo) references tb_promoCode(id);
 
-- insert 3 new audio payment policies for o2 consumer group

INSERT INTO tb_paymentPolicy (communityID,subWeeks,subCost,paymentType,operator,shortCode,currencyIso,availableInStore,app_store_product_id,contract,segment,content_category,content_type,content_description,sub_merchant_id,provider,tariff,media_type) VALUES
(10,5,5,'o2Psms',null,'','GBP',true,null,null,'CONSUMER','other','mqbed_tracks_3107056','Description of content','O2 Tracks','o2','_4G','AUDIO');


INSERT INTO tb_paymentPolicy (communityID,subWeeks,subCost,paymentType,operator,shortCode,currencyIso,availableInStore,app_store_product_id,contract,segment,content_category,content_type,content_description,sub_merchant_id,provider,tariff,media_type) VALUES
(10,2,2,'o2Psms',null,'','GBP',true,null,null,'CONSUMER','other','mqbed_tracks_3107055','Description of content','O2 Tracks','o2','_4G','AUDIO');


INSERT INTO tb_paymentPolicy (communityID,subWeeks,subCost,paymentType,operator,shortCode,currencyIso,availableInStore,app_store_product_id,contract,segment,content_category,content_type,content_description,sub_merchant_id,provider,tariff,media_type) VALUES
(10,1,1,'o2Psms',null,'','GBP',true,null,null,'CONSUMER','other','mqbed_tracks_3107054','Description of content','O2 Tracks','o2','_4G','AUDIO');


-- insert 3 new audio+video payment policies for o2 consumer group

INSERT INTO tb_paymentPolicy (communityID,subWeeks,subCost,paymentType,operator,shortCode,currencyIso,availableInStore,app_store_product_id,contract,segment,content_category,content_type,content_description,sub_merchant_id,provider,tariff,media_type) VALUES
(10,3,'4.5','o2Psms',null,'','GBP',true,null,null,'CONSUMER','other','mqbed_tracks_3107059','Description of content','O2 Tracks','o2','_4G','VIDEO_AND_AUDIO');


INSERT INTO tb_paymentPolicy (communityID,subWeeks,subCost,paymentType,operator,shortCode,currencyIso,availableInStore,app_store_product_id,contract,segment,content_category,content_type,content_description,sub_merchant_id,provider,tariff,media_type) VALUES
(10,2,'3','o2Psms',null,'','GBP',true,null,null,'CONSUMER','other','mqbed_tracks_3107058','Description of content','O2 Tracks','o2','_4G','VIDEO_AND_AUDIO');


INSERT INTO tb_paymentPolicy (communityID,subWeeks,subCost,paymentType,operator,shortCode,currencyIso,availableInStore,app_store_product_id,contract,segment,content_category,content_type,content_description,sub_merchant_id,provider,tariff,media_type) VALUES
(10,1,'1.5','o2Psms',null,'','GBP',true,null,null,'CONSUMER','other','mqbed_tracks_3107057','Description of content','O2 Tracks','o2','_4G','VIDEO_AND_AUDIO');

 -- insert new windows phone device type to database
INSERT INTO tb_deviceTypes (name) VALUES ('WINDOWS_PHONE');
