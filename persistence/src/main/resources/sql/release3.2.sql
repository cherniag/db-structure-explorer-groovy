-- http://jira.dev.now-technologies.mobi:8181/browse/CL-5132
-- Improved handling of unlicensed track purchasing > Server
alter table tb_media add column publishDate INT(10) UNSIGNED NOT NULL;

-- http://jira.dev.now-technologies.mobi:8181/browse/CL-5107 
-- CL-5107 Change in the subscription model to give 4 weeks for £1 if user subscribes within 48hrs - day 1 > Server

DROP TABLE IF EXISTS `tb_filter_params`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_filter_params` (
  `PUserHandsetFilter_id` tinyint(3) unsigned NOT NULL,
  `userHandset` varchar(255) DEFAULT NULL,
  `PUserStateFilter_id` tinyint(3) unsigned NOT NULL,
  `userStates` varchar(255) DEFAULT NULL,
  KEY `FK4699DC1C63E73F0D` (`PUserHandsetFilter_id`),
  KEY `FK4699DC1CE7442F0D` (`PUserStateFilter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

alter table tb_paymentDetails add column promotionPaymentPolicy_id bigint(20) DEFAULT NULL;

DROP TABLE IF EXISTS `tb_promotionPaymentPolicy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_promotionPaymentPolicy` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `promotion_i` tinyint(4) DEFAULT NULL,
  `subcost` decimal(19,2) DEFAULT NULL,
  `subweeks` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2E3D2FE63BEC8B6D` (`promotion_i`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `tb_promotionPaymentPolicy_tb_paymentPolicy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_promotionPaymentPolicy_tb_paymentPolicy` (
  `tb_promotionPaymentPolicy_id` bigint(20) NOT NULL,
  `paymentPolicies_i` smallint(6) NOT NULL,
  KEY `FK834CEA001966272C` (`tb_promotionPaymentPolicy_id`),
  KEY `FK834CEA00C875CF15` (`paymentPolicies_i`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `tb_promotions_tb_filter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_promotions_tb_filter` (
  `tb_promotions_i` tinyint(4) NOT NULL,
  `filters_id` tinyint(3) unsigned NOT NULL,
  KEY `FK23564B34CC35EB` (`tb_promotions_i`),
  KEY `FK23564BB8F3C13C` (`filters_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

alter table tb_users add column potentialPromotion_i tinyint(4) DEFAULT NULL;

alter table tb_promotions modify column maxUsers INTEGER UNSIGNED NOT NULL;
alter table tb_promotions modify column numUsers INTEGER UNSIGNED NOT NULL;

--
alter table tb_useriPhoneDetails drop index primary_key;
-- We have 2 identical indexes on tb_useriPhoneDetails('i')

create table tb_userAndroidDetails (i integer not null, nbUpdates integer, status integer not null, token varchar(255), usergroup integer, userUID integer, primary key (i)) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create unique index primary_key on tb_useriPhoneDetails(`i`);

create table `hibernate_sequences`( `sequence_name` VARCHAR(255), `sequence_next_hi_value` INT ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- CL-5397: Android Changes > My Account changes > Send promotion key to the client
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-5397
alter table tb_promotions add column label varchar(20);