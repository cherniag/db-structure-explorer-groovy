 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "5.0-SN", "5.0-SN");

-- insert VF NZ community
INSERT INTO tb_communities(i,name,appVersion,communityTypeID,displayName,assetName,rewriteURLParameter)
VALUES (11,'vfnz', 1, 10, 'VFNZ', 'vf_nz', 'vf_nz');

-- insert VF NZ userGroup
-- TODO this is not complete, we should change chart/news/drmpolicy ids to vf-nz specific
INSERT INTO tb_userGroups(name,community,chart,news,drmPolicy)
VALUES ('VF Chart',11,10,10,10);


-- insert payment policies for VF NZ users:
INSERT INTO tb_paymentPolicy
(communityID,subWeeks,subCost,paymentType,operator,shortCode,currencyIso,availableInStore,app_store_product_id,contract,segment,content_category,content_type,
content_description,sub_merchant_id,provider,tariff,media_type,is_default)
VALUES(11, 1, 1.5,'vfPsms',null,'','NZD',1,null,null,'CONSUMER',null,null,null,'VF NZ Tracks','vf','_3G','AUDIO', 1);


INSERT INTO tb_paymentPolicy
(communityID,subWeeks,subCost,paymentType,operator,shortCode,currencyIso,availableInStore,app_store_product_id,contract,segment,content_category,content_type,
content_description,sub_merchant_id,provider,tariff,media_type,is_default)
VALUES(11, 4, 6,'vfPsms',null,'','NZD',1,null,null,'CONSUMER',null,null,null,'VF NZ Tracks','vf','_3G','AUDIO', 0);

INSERT INTO tb_paymentPolicy
(communityID,subWeeks,subCost,paymentType,operator,shortCode,currencyIso,availableInStore,app_store_product_id,contract,segment,content_category,content_type,
content_description,sub_merchant_id,provider,tariff,media_type,is_default)
VALUES(11, 4, 8.29,'PAY_PAL',null,'','NZD',1,null,null,'BUSINESS',null,null,null,null,'vf','_3G','AUDIO', 0);

INSERT INTO tb_paymentPolicy
(communityID,subWeeks,subCost,paymentType,operator,shortCode,currencyIso,availableInStore,app_store_product_id,contract,segment,content_category,content_type,
content_description,sub_merchant_id,provider,tariff,media_type,is_default)
VALUES(11, 4, 8.29,'PAY_PAL',null,'','NZD',1,null,null,null,null,null,null,null,'non_vf','_3G','AUDIO', 0);


INSERT INTO tb_paymentPolicy
(communityID,subWeeks,subCost,paymentType,operator,shortCode,currencyIso,availableInStore,app_store_product_id,contract,segment,content_category,content_type,
content_description,sub_merchant_id,provider,tariff,media_type,is_default)
VALUES(11, 0, 8.29,'iTunesSubscription',null,'','NZD',1,'com.musicqubed.vfnz.subscription',null,null,null,null,null,null,'non_vf','_3G','AUDIO', 0);

-- http://jira.musicqubed.com/browse/IMP-2606
-- IMP-2606 - [Backend] Schedule the automatic unsubscription notifications to users for the time between 8am and 8 pm
 ALTER TABLE tb_users DROP FOREIGN KEY FKFAEDF4F766159605;
 ALTER TABLE tb_users DROP INDEX FKFAEDF4F766159605;

 ALTER TABLE tb_promotions DROP FOREIGN KEY FK81812EA166159605;
 ALTER TABLE tb_promotions DROP INDEX FK81812EA166159605;

 alter table tb_users modify userGroup int not null;
 alter table tb_userGroups change i id int not null auto_increment;
 alter table tb_promotions modify userGroup int not null;

 alter table tb_users add index tb_users_PK_userGroup (userGroup), add constraint tb_users_U_userGroup foreign key (userGroup) references tb_userGroups (id);
 alter table tb_promotions add index tb_promotions_PK_userGroup (userGroup), add constraint tb_promotions_U_userGroup foreign key (userGroup) references tb_userGroups (id);

 alter table messages drop FOREIGN KEY FKE475014C82282017;
 alter table messages drop INDEX FKE475014C82282017;
 alter table tb_usergroups drop FOREIGN KEY FK908D03F08E0982CF;
 alter table tb_usergroups drop INDEX FK908D03F08E0982CF;
 alter table tb_drmpolicy drop FOREIGN KEY FK65277B208E0982CF;
 alter table tb_drmpolicy drop INDEX FK65277B208E0982CF;
 alter table tb_news drop FOREIGN KEY FKA4FC0A048E0982CF;
 alter table tb_news drop INDEX FKA4FC0A048E0982CF;
 alter table community_charts drop FOREIGN KEY FK3410E96B82282017;
 alter table community_charts drop INDEX FK3410E96B82282017;
 alter table not_promoted_devices drop FOREIGN KEY FKE8EDF02E82282017;
 alter table not_promoted_devices drop INDEX FKE8EDF02E82282017;
 alter table offers drop FOREIGN KEY FKC337319782282017;
 alter table offers drop INDEX FKC337319782282017;
 alter table promoted_devices drop FOREIGN KEY FK335F984282282017;
 alter table promoted_devices drop INDEX FK335F984282282017;

 alter table tb_communities change i id int not null auto_increment;
 alter table messages modify community_id int DEFAULT NULL;
 alter table tb_userGroups modify community int DEFAULT NULL;
 alter table tb_drmPolicy modify community  int DEFAULT NULL;
 alter table tb_news modify community int DEFAULT NULL;
 alter table community_charts modify community_id int NOT NULL;
 alter table not_promoted_devices modify community_id int DEFAULT NULL;
 alter table offers modify community_id int DEFAULT NULL;
 alter table promoted_devices modify community_id int DEFAULT NULL;

 alter table messages add index messages_PK_community_id (community_id), add constraint messages_U_community_id foreign key (community_id) references tb_communities (id);
 alter table tb_userGroups add index tb_userGroups_PK_community (community), add constraint tb_userGroups_U_community foreign key (community) references tb_communities (id);
 alter table tb_drmPolicy add index tb_drmPolicy_PK_community (community), add constraint tb_drmPolicy_U_community foreign key (community) references tb_communities (id);
 alter table tb_news add index tb_news_PK_community (community), add constraint tb_news_U_community foreign key (community) references tb_communities (id);
 alter table community_charts add index community_charts_PK_community_id (community_id), add constraint community_charts_U_community_id foreign key (community_id) references tb_communities (id);
 alter table not_promoted_devices add index not_promoted_devices_PK_community_id (community_id), add constraint not_promoted_devices_U_community_id foreign key (community_id) references tb_communities (id);
 alter table promoted_devices add index offers_PK_community_id (community_id), add constraint offers_U_community_id foreign key (community_id) references tb_communities (id);
 alter table promoted_devices add index promoted_devices_PK_community_id (community_id), add constraint promoted_devices_U_community_id foreign key (community_id) references tb_communities (id);
