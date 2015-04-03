CREATE DATABASE  IF NOT EXISTS `cn_service_at` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci */;

SET foreign_key_checks = 0;

USE `cn_service_at`;
-- MySQL dump 10.13  Distrib 5.6.13, for Win32 (x86)
--
-- Host: db01.musicqubed.com    Database: cn_service
-- ------------------------------------------------------
-- Server version	5.1.73-rel14.11-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `refund`
--

DROP TABLE IF EXISTS `refund`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `refund` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `log_time_millis` bigint(20) DEFAULT NULL,
  `next_sub_payment_millis` bigint(20) DEFAULT NULL,
  `payment_details_id` bigint(20) NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `reason` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `refund_PK_payment_details_id` (`payment_details_id`),
  KEY `refund_PK_user_id` (`user_id`),
  CONSTRAINT `refund_U_payment_details_id` FOREIGN KEY (`payment_details_id`) REFERENCES `tb_paymentDetails` (`i`),
  CONSTRAINT `refund_U_user_id` FOREIGN KEY (`user_id`) REFERENCES `tb_users` (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=1455 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_deviceTypes`
--

DROP TABLE IF EXISTS `tb_deviceTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_deviceTypes` (
  `i` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(25) NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `not_promoted_devices`
--

DROP TABLE IF EXISTS `not_promoted_devices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `not_promoted_devices` (
  `deviceUID` varchar(255) NOT NULL,
  `community_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`deviceUID`),
  UNIQUE KEY `DEVICE_UID_INDEX` (`deviceUID`),
  KEY `not_promoted_devices_PK_community_id` (`community_id`),
  CONSTRAINT `not_promoted_devices_U_community_id` FOREIGN KEY (`community_id`) REFERENCES `tb_communities` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `old_users11`
--

DROP TABLE IF EXISTS `old_users11`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `old_users11` (
  `i` int(10) unsigned NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_JOB_DETAILS`
--

DROP TABLE IF EXISTS `QRTZ_JOB_DETAILS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_JOB_DETAILS` (
  `JOB_NAME` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `JOB_GROUP` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `DESCRIPTION` varchar(250) COLLATE utf8_unicode_ci DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) COLLATE utf8_unicode_ci NOT NULL,
  `IS_DURABLE` varchar(1) COLLATE utf8_unicode_ci NOT NULL,
  `IS_VOLATILE` varchar(1) COLLATE utf8_unicode_ci NOT NULL,
  `IS_STATEFUL` varchar(1) COLLATE utf8_unicode_ci NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) COLLATE utf8_unicode_ci NOT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`JOB_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_drm`
--

DROP TABLE IF EXISTS `tb_drm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_drm` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user` int(10) unsigned NOT NULL,
  `media` int(10) unsigned NOT NULL,
  `drmType` tinyint(3) unsigned NOT NULL,
  `drmValue` tinyint(3) unsigned NOT NULL,
  `timestamp` int(10) unsigned NOT NULL,
  PRIMARY KEY (`i`),
  KEY `FKCB83DC4E8A438D0F` (`drmType`),
  KEY `drnUser` (`user`)
) ENGINE=InnoDB AUTO_INCREMENT=199471475 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `social_info`
--

DROP TABLE IF EXISTS `social_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `social_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `social_info_user_id` (`user_id`),
  CONSTRAINT `social_info_user_id` FOREIGN KEY (`user_id`) REFERENCES `tb_users` (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=720907 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `filters`
--

DROP TABLE IF EXISTS `filters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `filters` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_files`
--

DROP TABLE IF EXISTS `tb_files`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_files` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `filename` char(40) NOT NULL,
  `size` int(10) unsigned NOT NULL,
  `fileType` tinyint(3) unsigned NOT NULL,
  `version` int(11) NOT NULL,
  `duration` int(10) unsigned NOT NULL,
  PRIMARY KEY (`i`),
  UNIQUE KEY `filename` (`filename`),
  KEY `FKFA162166A2224AEF` (`fileType`),
  CONSTRAINT `FKFA162166A2224AEF` FOREIGN KEY (`fileType`) REFERENCES `tb_fileTypes` (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=49278 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Territory`
--

DROP TABLE IF EXISTS `Territory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Territory` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `Code` varchar(255) NOT NULL,
  `Distributor` varchar(255) NOT NULL,
  `Currency` varchar(255) DEFAULT NULL,
  `Price` float DEFAULT NULL,
  `PriceCode` varchar(255) DEFAULT NULL,
  `StartDate` date DEFAULT NULL,
  `ReportingId` varchar(255) DEFAULT NULL,
  `DealReference` varchar(255) DEFAULT NULL,
  `Label` varchar(255) NOT NULL,
  `TrackId` bigint(20) DEFAULT NULL,
  `Publisher` varchar(255) DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `DeleteDate` date DEFAULT NULL,
  `CreateDate` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKF95807FAF2C0F03A` (`TrackId`),
  CONSTRAINT `FKF95807FAF2C0F03A` FOREIGN KEY (`TrackId`) REFERENCES `Track` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `auto_opt_in_exempt_ph_number`
--

DROP TABLE IF EXISTS `auto_opt_in_exempt_ph_number`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `auto_opt_in_exempt_ph_number` (
  `phone` varchar(255) NOT NULL,
  PRIMARY KEY (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_JOB_LISTENERS`
--

DROP TABLE IF EXISTS `QRTZ_JOB_LISTENERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_JOB_LISTENERS` (
  `JOB_NAME` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `JOB_GROUP` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `JOB_LISTENER` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`JOB_NAME`,`JOB_GROUP`,`JOB_LISTENER`),
  KEY `JOB_NAME` (`JOB_NAME`,`JOB_GROUP`),
  CONSTRAINT `QRTZ_JOB_LISTENERS_ibfk_1` FOREIGN KEY (`JOB_NAME`, `JOB_GROUP`) REFERENCES `QRTZ_JOB_DETAILS` (`JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_promoCode`
--

DROP TABLE IF EXISTS `tb_promoCode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_promoCode` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `promotionId` int(11) DEFAULT NULL,
  `media_type` char(255) NOT NULL DEFAULT 'AUDIO',
  PRIMARY KEY (`id`),
  KEY `FKF393230B3BEC8C9E` (`promotionId`),
  KEY `tb_promocode_PK_promotionId` (`promotionId`),
  CONSTRAINT `tb_promocode_U_promotionId` FOREIGN KEY (`promotionId`) REFERENCES `tb_promotions` (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hibernate_sequences`
--

DROP TABLE IF EXISTS `hibernate_sequences`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hibernate_sequences` (
  `sequence_name` varchar(255) DEFAULT NULL,
  `sequence_next_hi_value` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_adminUserType`
--

DROP TABLE IF EXISTS `tb_adminUserType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_adminUserType` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `description` varchar(50) NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `activation_emails`
--

DROP TABLE IF EXISTS `activation_emails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activation_emails` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activated` bit(1) DEFAULT b'0',
  `deviceUID` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `token` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `activation_emails_email_deviceUID_token` (`email`,`deviceUID`,`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_userStatus`
--

DROP TABLE IF EXISTS `tb_userStatus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_userStatus` (
  `i` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(25) NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_users`
--

DROP TABLE IF EXISTS `tb_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_users` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `displayName` char(25) NOT NULL,
  `title` char(10) NOT NULL,
  `firstName` char(40) NOT NULL,
  `lastName` char(40) NOT NULL,
  `userName` char(50) NOT NULL,
  `subBalance` int(11) NOT NULL,
  `freeBalance` tinyint(3) unsigned DEFAULT NULL,
  `token` char(40) NOT NULL,
  `status` tinyint(3) unsigned NOT NULL,
  `deviceType` tinyint(3) unsigned NOT NULL,
  `device` char(40) NOT NULL,
  `userGroup` int(11) NOT NULL,
  `userType` int(10) unsigned NOT NULL DEFAULT '0',
  `lastDeviceLogin` int(10) unsigned NOT NULL,
  `lastWebLogin` int(10) unsigned NOT NULL,
  `nextSubPayment` int(10) unsigned NOT NULL,
  `lastPaymentTx` int(10) unsigned NOT NULL,
  `Address1` char(50) NOT NULL,
  `Address2` char(50) NOT NULL,
  `City` char(20) NOT NULL,
  `Postcode` char(15) NOT NULL,
  `Country` smallint(5) unsigned NOT NULL,
  `mobile` char(15) NOT NULL,
  `code` char(40) NOT NULL,
  `sessionID` char(40) NOT NULL,
  `ipAddress` char(40) NOT NULL,
  `tempToken` char(40) NOT NULL,
  `deviceString` char(100) NOT NULL,
  `canContact` tinyint(1) NOT NULL,
  `paymentType` varchar(255) DEFAULT 'UNKNOWN',
  `operator` int(10) unsigned NOT NULL,
  `pin` varchar(10) NOT NULL,
  `paymentEnabled` tinyint(1) NOT NULL DEFAULT '0',
  `paymentStatus` int(10) unsigned NOT NULL,
  `numPsmsRetries` int(10) unsigned NOT NULL,
  `age` tinyint(3) unsigned DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `facebookId` varchar(45) DEFAULT NULL,
  `lastSuccessfulPaymentTimeMillis` bigint(20) DEFAULT '0',
  `currentPaymentDetailsId` bigint(20) DEFAULT NULL,
  `deviceUID` varchar(255) DEFAULT NULL,
  `deviceModel` varchar(255) DEFAULT NULL,
  `potentialPromotion_i` int(11) DEFAULT NULL,
  `potentialPromoCodePromotion_i` int(11) DEFAULT NULL,
  `firstDeviceLoginMillis` bigint(20) DEFAULT NULL,
  `firstUserLoginMillis` bigint(20) DEFAULT NULL,
  `isFreeTrial` bit(1) NOT NULL DEFAULT b'0',
  `amountOfMoneyToUserNotification` decimal(5,2) NOT NULL DEFAULT '0.00',
  `lastSuccesfullPaymentSmsSendingTimestampMillis` bigint(20) NOT NULL DEFAULT '0',
  `freeTrialExpiredMillis` bigint(20) DEFAULT NULL,
  `freeTrialStartedTimestampMillis` bigint(20) DEFAULT NULL,
  `activation_status` varchar(255) DEFAULT NULL,
  `provider` varchar(255) DEFAULT NULL,
  `contract` varchar(255) DEFAULT NULL,
  `base64_encoded_app_store_receipt` longtext,
  `app_store_original_transaction_id` varchar(255) DEFAULT NULL,
  `last_subscribed_payment_system` varchar(255) DEFAULT NULL,
  `segment` char(255) DEFAULT NULL,
  `last_before48_sms_millis` bigint(20) DEFAULT '0',
  `tariff` char(255) NOT NULL DEFAULT '_3G',
  `video_free_trial_has_been_activated` tinyint(1) NOT NULL DEFAULT '0',
  `last_successful_payment_details_id` bigint(20) DEFAULT NULL,
  `contract_channel` varchar(255) DEFAULT 'DIRECT',
  `last_promo` int(10) DEFAULT NULL,
  `idfa` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`i`),
  UNIQUE KEY `userName` (`userName`,`userGroup`),
  UNIQUE KEY `deviceUID` (`deviceUID`,`userGroup`),
  KEY `FKFAEDF4F753C7738F` (`potentialPromoCodePromotion_i`),
  KEY `FKFAEDF4F7BB117032` (`status`),
  KEY `FKFAEDF4F7EE93D5E3` (`deviceType`),
  KEY `FKFAEDF4F72FD62F8F` (`currentPaymentDetailsId`),
  KEY `facebookId` (`facebookId`),
  KEY `lastDeviceLogin` (`lastDeviceLogin`),
  KEY `mobile` (`mobile`),
  KEY `tb_users_PK_last_successful_payment_details` (`last_successful_payment_details_id`),
  KEY `tb_users_PK_potentialPromoCodePromotion_i` (`potentialPromoCodePromotion_i`),
  KEY `user_promo_code_fk` (`last_promo`),
  KEY `tb_users_PK_userGroup` (`userGroup`),
  CONSTRAINT `FKFAEDF4F72FD62F8F` FOREIGN KEY (`currentPaymentDetailsId`) REFERENCES `tb_paymentDetails` (`i`),
  CONSTRAINT `FKFAEDF4F7BB117032` FOREIGN KEY (`status`) REFERENCES `tb_userStatus` (`i`),
  CONSTRAINT `FKFAEDF4F7EE93D5E3` FOREIGN KEY (`deviceType`) REFERENCES `tb_deviceTypes` (`i`),
  CONSTRAINT `tb_users_U_last_successful_payment_details` FOREIGN KEY (`last_successful_payment_details_id`) REFERENCES `tb_paymentDetails` (`i`),
  CONSTRAINT `tb_users_U_potentialPromoCodePromotion_i` FOREIGN KEY (`potentialPromoCodePromotion_i`) REFERENCES `tb_promotions` (`i`),
  CONSTRAINT `tb_users_U_userGroup` FOREIGN KEY (`userGroup`) REFERENCES `tb_userGroups` (`id`),
  CONSTRAINT `user_promo_code_fk` FOREIGN KEY (`last_promo`) REFERENCES `tb_promoCode` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2743714 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_tasks`
--

DROP TABLE IF EXISTS `tb_tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_tasks` (
  `taskType` varchar(50) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationTimestamp` bigint(20) NOT NULL,
  `executionTimestamp` bigint(20) NOT NULL,
  `taskStatus` char(25) DEFAULT NULL,
  `user_id` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tb_tasks_pk` (`user_id`),
  CONSTRAINT `tbTasksUserId_tbUsersI_fk` FOREIGN KEY (`user_id`) REFERENCES `tb_users` (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=1400 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `AssetFile`
--

DROP TABLE IF EXISTS `AssetFile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AssetFile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` int(11) DEFAULT NULL,
  `path` varchar(255) NOT NULL,
  `MD5` varchar(255) DEFAULT NULL,
  `TrackId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKC5D17C6CF2C0F03A` (`TrackId`),
  CONSTRAINT `FKC5D17C6CF2C0F03A` FOREIGN KEY (`TrackId`) REFERENCES `Track` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `backup_tb_accountLog_140106`
--

DROP TABLE IF EXISTS `backup_tb_accountLog_140106`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `backup_tb_accountLog_140106` (
  `i` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `userUID` int(10) unsigned NOT NULL,
  `transactionType` int(11) NOT NULL,
  `balanceAfter` int(11) NOT NULL,
  `relatedMediaUID` int(10) unsigned DEFAULT NULL,
  `relatedPaymentUID` bigint(20) DEFAULT NULL,
  `logTimestamp` int(11) NOT NULL,
  `topupBalance` int(10) DEFAULT NULL,
  `amountUnused` int(10) DEFAULT NULL,
  `relatedTopup` int(10) DEFAULT NULL,
  `promoCode` varchar(255) DEFAULT NULL,
  `description` varchar(10000) DEFAULT NULL,
  PRIMARY KEY (`i`),
  KEY `user` (`userUID`)
) ENGINE=InnoDB AUTO_INCREMENT=12931574 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_submittedPayments`
--

DROP TABLE IF EXISTS `tb_submittedPayments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_submittedPayments` (
  `i` bigint(20) NOT NULL AUTO_INCREMENT,
  `amount` decimal(19,2) DEFAULT NULL,
  `currencyISO` varchar(255) DEFAULT NULL,
  `internalTxId` varchar(255) DEFAULT NULL,
  `paymentSystem` varchar(255) DEFAULT NULL,
  `subweeks` int(11) NOT NULL,
  `timestamp` bigint(20) DEFAULT '0',
  `status` varchar(255) DEFAULT NULL,
  `descriptionError` varchar(255) DEFAULT NULL,
  `userId` int(11) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `externalTxId` varchar(255) NOT NULL,
  `paymentDetailsId` bigint(20) DEFAULT NULL,
  `next_sub_payment` int(11) NOT NULL,
  `base64_encoded_app_store_receipt` longtext,
  `app_store_original_transaction_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`i`),
  KEY `userid` (`userId`),
  KEY `FKF1EF29B7197A82E5` (`paymentDetailsId`),
  CONSTRAINT `FKF1EF29B7197A82E5` FOREIGN KEY (`paymentDetailsId`) REFERENCES `tb_paymentDetails` (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=1961619 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_useriPhoneDetails`
--

DROP TABLE IF EXISTS `tb_useriPhoneDetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_useriPhoneDetails` (
  `i` int(10) NOT NULL AUTO_INCREMENT,
  `userUID` int(10) unsigned NOT NULL,
  `token` varchar(64) DEFAULT NULL,
  `usergroup` int(10) DEFAULT NULL,
  `nbUpdates` int(5) DEFAULT NULL,
  `status` int(3) DEFAULT NULL,
  `threadId` varchar(50) DEFAULT NULL,
  `last_push_of_content_update_millis` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`i`),
  UNIQUE KEY `primary_key` (`i`),
  KEY `INDEX_ON_IPHONE_USERUID_TB_USER_I` (`userUID`),
  CONSTRAINT `INDEX_ON_IPHONE_USERUID_TB_USER_I` FOREIGN KEY (`userUID`) REFERENCES `tb_users` (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=1290734 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_page_menu_items`
--

DROP TABLE IF EXISTS `tb_page_menu_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_page_menu_items` (
  `i` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `menu` tinyint(3) unsigned NOT NULL,
  `label` char(40) NOT NULL,
  `internalLink` smallint(5) unsigned NOT NULL,
  `internalParams` char(40) NOT NULL,
  `submenu` tinyint(3) unsigned NOT NULL,
  `position` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_filter_params`
--

DROP TABLE IF EXISTS `tb_filter_params`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_filter_params` (
  `PUserHandsetFilter_id` tinyint(3) unsigned NOT NULL,
  `userHandset` varchar(255) DEFAULT NULL,
  `PUserStateFilter_id` tinyint(3) unsigned NOT NULL,
  `userStates` varchar(255) DEFAULT NULL,
  `activeSinceTrialStartTimestampMillis` bigint(20) DEFAULT NULL,
  `activeTillTrialEndTimestampMillis` bigint(20) DEFAULT NULL,
  `FreeTrialPeriodFilter_id` tinyint(3) unsigned NOT NULL,
  KEY `FK4699DC1C63E73F0D` (`PUserHandsetFilter_id`),
  KEY `FK4699DC1CE7442F0D` (`PUserStateFilter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_appVersionCountry`
--

DROP TABLE IF EXISTS `tb_appVersionCountry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_appVersionCountry` (
  `appVersion_id` tinyint(3) unsigned NOT NULL,
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `country_id` smallint(5) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `FK37C6208EA1680037` (`country_id`),
  KEY `FK37C6208EA3500ADD` (`appVersion_id`),
  CONSTRAINT `FK37C6208EA1680037` FOREIGN KEY (`country_id`) REFERENCES `tb_country` (`i`),
  CONSTRAINT `FK37C6208EA3500ADD` FOREIGN KEY (`appVersion_id`) REFERENCES `tb_appVersions` (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=259 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_promotedDevice`
--

DROP TABLE IF EXISTS `tb_promotedDevice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_promotedDevice` (
  `deviceUID` varchar(255) NOT NULL,
  PRIMARY KEY (`deviceUID`),
  UNIQUE KEY `DEVICE_UID_INDEX` (`deviceUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_country`
--

DROP TABLE IF EXISTS `tb_country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_country` (
  `i` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(10) NOT NULL,
  `fullName` varchar(200) NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=241 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_paymentDetails`
--

DROP TABLE IF EXISTS `tb_paymentDetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_paymentDetails` (
  `paymentType` varchar(31) NOT NULL,
  `i` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationTimestampMillis` bigint(20) NOT NULL,
  `descriptionError` varchar(255) DEFAULT NULL,
  `disableTimestampMillis` bigint(20) NOT NULL,
  `lastPaymentStatus` varchar(255) DEFAULT NULL,
  `madeRetries` int(11) NOT NULL,
  `VPSTxId` varchar(255) DEFAULT NULL,
  `released` bit(1) DEFAULT NULL,
  `securityKey` varchar(255) DEFAULT NULL,
  `txAuthNo` varchar(255) DEFAULT NULL,
  `vendorTxCode` varchar(255) DEFAULT NULL,
  `paymentPolicyId` int(11) DEFAULT NULL,
  `retriesOnError` int(11) NOT NULL,
  `billingAgreementTxId` varchar(255) DEFAULT NULL,
  `migPhoneNumber` varchar(255) DEFAULT NULL,
  `activated` bit(1) NOT NULL DEFAULT b'1',
  `promotionPaymentPolicy_id` bigint(20) DEFAULT NULL,
  `owner_id` int(10) unsigned DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `errorCode` varchar(255) DEFAULT NULL,
  `last_failed_payment_notification_millis` bigint(20) DEFAULT NULL,
  `made_attempts` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`i`),
  KEY `owner_payment_details` (`owner_id`),
  KEY `FK60BC1B4D5A008DBD` (`promotionPaymentPolicy_id`),
  KEY `FK60BC1B4DC604BCD5` (`owner_id`),
  KEY `tb_paymentDetails_PK_paymentPolicyId` (`paymentPolicyId`),
  CONSTRAINT `FK60BC1B4D5A008DBD` FOREIGN KEY (`promotionPaymentPolicy_id`) REFERENCES `tb_promotionPaymentPolicy` (`id`),
  CONSTRAINT `FK60BC1B4DC604BCD5` FOREIGN KEY (`owner_id`) REFERENCES `tb_users` (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=347024 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_charts`
--

DROP TABLE IF EXISTS `user_charts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_charts` (
  `user_id` int(10) unsigned NOT NULL,
  `chart_id` tinyint(4) NOT NULL,
  KEY `FK_user_id` (`user_id`),
  KEY `FK_chart_id` (`chart_id`),
  CONSTRAINT `FK_chart_id` FOREIGN KEY (`chart_id`) REFERENCES `tb_charts` (`i`),
  CONSTRAINT `FK_user_id` FOREIGN KEY (`user_id`) REFERENCES `tb_users` (`i`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `for_refund_tb_submittedPayments`
--

DROP TABLE IF EXISTS `for_refund_tb_submittedPayments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `for_refund_tb_submittedPayments` (
  `i` bigint(20) NOT NULL,
  `amount` decimal(19,2) DEFAULT NULL,
  `currencyISO` varchar(255) DEFAULT NULL,
  `internalTxId` varchar(255) DEFAULT NULL,
  `paymentSystem` varchar(255) DEFAULT NULL,
  `subweeks` int(11) NOT NULL,
  `timestamp` bigint(20) DEFAULT '0',
  `status` varchar(255) DEFAULT NULL,
  `descriptionError` varchar(255) DEFAULT NULL,
  `userId` int(11) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `externalTxId` varchar(255) NOT NULL,
  `paymentDetailsId` bigint(20) DEFAULT NULL,
  `next_sub_payment` int(11) NOT NULL,
  `base64_encoded_app_store_receipt` longtext,
  `app_store_original_transaction_id` varchar(255) DEFAULT NULL,
  `recordCreationTimestamp` bigint(20) NOT NULL DEFAULT '0',
  `specialComment` char(50) DEFAULT NULL,
  PRIMARY KEY (`i`,`recordCreationTimestamp`),
  KEY `specialComment` (`specialComment`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_charts`
--

DROP TABLE IF EXISTS `tb_charts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_charts` (
  `i` tinyint(4) NOT NULL AUTO_INCREMENT,
  `name` char(25) NOT NULL,
  `numTracks` tinyint(3) unsigned NOT NULL,
  `genre` int(10) unsigned NOT NULL DEFAULT '0',
  `timestamp` int(11) NOT NULL,
  `numBonusTracks` tinyint(4) NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`i`),
  KEY `FK437C9B46FE903C83` (`genre`),
  CONSTRAINT `FK437C9B46FE903C83` FOREIGN KEY (`genre`) REFERENCES `tb_genres` (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_userGroups`
--

DROP TABLE IF EXISTS `tb_userGroups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_userGroups` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` char(25) NOT NULL,
  `community` int(11) DEFAULT NULL,
  `chart` tinyint(3) unsigned NOT NULL,
  `news` tinyint(3) unsigned NOT NULL,
  `drmPolicy` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK908D03F063134889` (`news`),
  KEY `FK908D03F0F8D07E3F` (`drmPolicy`),
  KEY `tb_userGroups_PK_community` (`community`),
  CONSTRAINT `FK908D03F063134889` FOREIGN KEY (`news`) REFERENCES `tb_news` (`i`),
  CONSTRAINT `FK908D03F0F8D07E3F` FOREIGN KEY (`drmPolicy`) REFERENCES `tb_drmPolicy` (`i`),
  CONSTRAINT `tb_userGroups_U_community` FOREIGN KEY (`community`) REFERENCES `tb_communities` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_paymentPolicyNow`
--

DROP TABLE IF EXISTS `tb_paymentPolicyNow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_paymentPolicyNow` (
  `i` smallint(5) unsigned NOT NULL DEFAULT '0',
  `communityID` int(10) unsigned NOT NULL,
  `subWeeks` tinyint(3) unsigned NOT NULL,
  `subCost` char(5) NOT NULL,
  `paymentType` varchar(50) NOT NULL,
  `operator` int(10) unsigned DEFAULT NULL,
  `shortCode` varchar(20) NOT NULL,
  `currencyIso` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_adminPermissionsLabel`
--

DROP TABLE IF EXISTS `tb_adminPermissionsLabel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_adminPermissionsLabel` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `description` varchar(50) NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_drmPolicy`
--

DROP TABLE IF EXISTS `tb_drmPolicy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_drmPolicy` (
  `i` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(25) NOT NULL,
  `drmType` tinyint(3) unsigned NOT NULL,
  `drmValue` tinyint(3) unsigned NOT NULL,
  `community` int(11) DEFAULT NULL,
  PRIMARY KEY (`i`),
  KEY `FK65277B208A438D0F` (`drmType`),
  KEY `tb_drmPolicy_PK_community` (`community`),
  CONSTRAINT `FK65277B208A438D0F` FOREIGN KEY (`drmType`) REFERENCES `tb_drmTypes` (`i`),
  CONSTRAINT `tb_drmPolicy_U_community` FOREIGN KEY (`community`) REFERENCES `tb_communities` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_adminUsers`
--

DROP TABLE IF EXISTS `tb_adminUsers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_adminUsers` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `firstName` varchar(40) NOT NULL,
  `lastName` varchar(40) NOT NULL,
  `userID` varchar(40) NOT NULL,
  `accessLevel` tinyint(4) NOT NULL DEFAULT '0',
  `adminUserTypeID` int(10) unsigned NOT NULL DEFAULT '100',
  `password` varchar(40) NOT NULL,
  `sessionID` varchar(40) NOT NULL,
  `lastUse` varchar(40) NOT NULL,
  `ipAddress` varchar(40) NOT NULL,
  PRIMARY KEY (`i`),
  UNIQUE KEY `userID` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_TRIGGERS` (
  `TRIGGER_NAME` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `JOB_NAME` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `JOB_GROUP` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `IS_VOLATILE` varchar(1) COLLATE utf8_unicode_ci NOT NULL,
  `DESCRIPTION` varchar(250) COLLATE utf8_unicode_ci DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) COLLATE utf8_unicode_ci NOT NULL,
  `TRIGGER_TYPE` varchar(8) COLLATE utf8_unicode_ci NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `MISFIRE_INSTR` smallint(2) DEFAULT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `JOB_NAME` (`JOB_NAME`,`JOB_GROUP`),
  CONSTRAINT `QRTZ_TRIGGERS_ibfk_1` FOREIGN KEY (`JOB_NAME`, `JOB_GROUP`) REFERENCES `QRTZ_JOB_DETAILS` (`JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_CRON_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_CRON_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_CRON_TRIGGERS` (
  `TRIGGER_NAME` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `CRON_EXPRESSION` varchar(120) COLLATE utf8_unicode_ci NOT NULL,
  `TIME_ZONE_ID` varchar(80) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `TRIGGER_NAME` (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `QRTZ_CRON_TRIGGERS_ibfk_1` FOREIGN KEY (`TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_payments`
--

DROP TABLE IF EXISTS `tb_payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_payments` (
  `i` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `userUID` int(10) unsigned NOT NULL,
  `internalTxCode` char(40) NOT NULL,
  `txType` int(10) unsigned NOT NULL,
  `amount` float unsigned NOT NULL,
  `description` char(100) NOT NULL,
  `status` char(15) NOT NULL,
  `statusDetail` char(255) NOT NULL,
  `externalTxCode` char(38) NOT NULL,
  `externalSecurityKey` char(20) NOT NULL,
  `externalAuthCode` char(20) NOT NULL,
  `timestamp` int(10) unsigned NOT NULL,
  `relatedPayment` bigint(20) unsigned NOT NULL,
  `paymentType` varchar(20) NOT NULL,
  `subweeks` tinyint(3) unsigned NOT NULL,
  `numPaymentRetries` int(10) unsigned DEFAULT NULL,
  `currencyCode` varchar(5) DEFAULT NULL,
  `paymentGateway` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`i`),
  KEY `pmt_user` (`userUID`),
  KEY `pmtUserStatus` (`status`,`userUID`),
  KEY `pmtTxCode` (`internalTxCode`)
) ENGINE=InnoDB AUTO_INCREMENT=3437656 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `backup_tb_submittedPayments_140106`
--

DROP TABLE IF EXISTS `backup_tb_submittedPayments_140106`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `backup_tb_submittedPayments_140106` (
  `i` bigint(20) NOT NULL AUTO_INCREMENT,
  `amount` decimal(19,2) DEFAULT NULL,
  `currencyISO` varchar(255) DEFAULT NULL,
  `internalTxId` varchar(255) DEFAULT NULL,
  `paymentSystem` varchar(255) DEFAULT NULL,
  `subweeks` int(11) NOT NULL,
  `timestamp` bigint(20) DEFAULT '0',
  `status` varchar(255) DEFAULT NULL,
  `descriptionError` varchar(255) DEFAULT NULL,
  `userId` int(11) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `externalTxId` varchar(255) NOT NULL,
  `paymentDetailsId` bigint(20) DEFAULT NULL,
  `next_sub_payment` int(11) NOT NULL,
  `base64_encoded_app_store_receipt` longtext,
  `app_store_original_transaction_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`i`),
  KEY `userid` (`userId`),
  KEY `FKF1EF29B7197A82E5` (`paymentDetailsId`)
) ENGINE=InnoDB AUTO_INCREMENT=739495 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_paymentStatus`
--

DROP TABLE IF EXISTS `tb_paymentStatus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_paymentStatus` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(25) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_promotions`
--

DROP TABLE IF EXISTS `tb_promotions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_promotions` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `description` char(100) NOT NULL,
  `numUsers` int(10) unsigned NOT NULL,
  `maxUsers` int(10) unsigned NOT NULL,
  `startDate` int(10) unsigned NOT NULL,
  `endDate` int(10) unsigned NOT NULL,
  `isActive` tinyint(1) NOT NULL,
  `freeWeeks` tinyint(3) unsigned NOT NULL,
  `subWeeks` tinyint(3) unsigned NOT NULL,
  `userGroup` int(11) NOT NULL,
  `type` char(20) NOT NULL,
  `showPromotion` tinyint(1) NOT NULL,
  `label` varchar(50) DEFAULT NULL,
  `is_white_listed` bit(1) DEFAULT b'0',
  PRIMARY KEY (`i`),
  KEY `tb_promotions_PK_userGroup` (`userGroup`),
  CONSTRAINT `tb_promotions_U_userGroup` FOREIGN KEY (`userGroup`) REFERENCES `tb_userGroups` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_menuItems`
--

DROP TABLE IF EXISTS `tb_menuItems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_menuItems` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `label` varchar(40) NOT NULL,
  `menu` smallint(5) unsigned NOT NULL,
  `internalLink` smallint(5) unsigned NOT NULL DEFAULT '0',
  `internalParams` varchar(40) NOT NULL,
  `submenu` smallint(5) unsigned NOT NULL DEFAULT '0',
  `position` tinyint(3) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_userAndroidDetails`
--

DROP TABLE IF EXISTS `tb_userAndroidDetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_userAndroidDetails` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `nbUpdates` int(11) DEFAULT NULL,
  `status` int(11) NOT NULL,
  `token` varchar(255) DEFAULT NULL,
  `usergroup` int(11) DEFAULT NULL,
  `userUID` int(10) unsigned NOT NULL,
  `last_push_of_content_update_millis` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`i`),
  KEY `INDEX_ON_ANDROID_USERUID_TB_USER_I` (`userUID`),
  CONSTRAINT `INDEX_ON_ANDROID_USERUID_TB_USER_I` FOREIGN KEY (`userUID`) REFERENCES `tb_users` (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=1326399 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_pages`
--

DROP TABLE IF EXISTS `tb_pages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_pages` (
  `i` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(40) NOT NULL,
  `title` char(40) NOT NULL,
  `accessLevel` tinyint(3) unsigned NOT NULL,
  `mainMenu` smallint(5) unsigned NOT NULL,
  `subMenu` smallint(5) unsigned NOT NULL,
  `userMenu` smallint(5) unsigned NOT NULL,
  `contentModule` smallint(5) unsigned NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oldpmt`
--

DROP TABLE IF EXISTS `oldpmt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oldpmt` (
  `i` bigint(20) unsigned NOT NULL DEFAULT '0',
  `userUID` int(10) unsigned NOT NULL,
  `internalTxCode` char(40) NOT NULL,
  `txType` int(10) unsigned NOT NULL,
  `amount` float unsigned NOT NULL,
  `description` char(100) NOT NULL,
  `status` char(15) NOT NULL,
  `statusDetail` char(255) NOT NULL,
  `externalTxCode` char(38) NOT NULL,
  `externalSecurityKey` char(20) NOT NULL,
  `externalAuthCode` char(20) NOT NULL,
  `timestamp` int(10) unsigned NOT NULL,
  `relatedPayment` bigint(20) unsigned NOT NULL,
  `paymentType` varchar(20) NOT NULL,
  `subweeks` tinyint(3) unsigned NOT NULL,
  `numPaymentRetries` int(10) unsigned DEFAULT NULL,
  `currencyCode` varchar(5) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_newsDetail`
--

DROP TABLE IF EXISTS `tb_newsDetail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_newsDetail` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `news` int(10) unsigned NOT NULL,
  `position` tinyint(3) unsigned NOT NULL,
  `item` char(255) NOT NULL,
  `body` text,
  `online` bit(1) NOT NULL DEFAULT b'1',
  `userHandset` char(15) DEFAULT NULL,
  `userState` char(50) DEFAULT NULL,
  `messageFrequence` char(30) DEFAULT NULL,
  `messageType` char(15) NOT NULL,
  `timestampMilis` bigint(20) NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_banned`
--

DROP TABLE IF EXISTS `user_banned`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_banned` (
  `user_id` int(11) NOT NULL,
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `giveAnyPromotion` tinyint(1) NOT NULL,
  `timestamp` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_PAUSED_TRIGGER_GRPS`
--

DROP TABLE IF EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_PAUSED_TRIGGER_GRPS` (
  `TRIGGER_GROUP` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_promotionPaymentPolicy_tb_paymentPolicy`
--

DROP TABLE IF EXISTS `tb_promotionPaymentPolicy_tb_paymentPolicy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_promotionPaymentPolicy_tb_paymentPolicy` (
  `tb_promotionPaymentPolicy_id` bigint(20) NOT NULL,
  `paymentPolicies_i` int(11) DEFAULT NULL,
  KEY `FK834CEA001966272C` (`tb_promotionPaymentPolicy_id`),
  KEY `tb_promotionPaymentPolicy_tb_paymentPolicy_PK_paymentPolicies_i` (`paymentPolicies_i`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_mediaLog`
--

DROP TABLE IF EXISTS `tb_mediaLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_mediaLog` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `userUID` int(10) unsigned NOT NULL,
  `mediaUID` int(10) unsigned NOT NULL,
  `logType` tinyint(3) unsigned NOT NULL,
  `logTimestamp` int(10) unsigned NOT NULL,
  PRIMARY KEY (`i`),
  KEY `useruid` (`userUID`)
) ENGINE=InnoDB AUTO_INCREMENT=36624541 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_TRIGGER_LISTENERS`
--

DROP TABLE IF EXISTS `QRTZ_TRIGGER_LISTENERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_TRIGGER_LISTENERS` (
  `TRIGGER_NAME` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `TRIGGER_LISTENER` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_LISTENER`),
  KEY `TRIGGER_NAME` (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `QRTZ_TRIGGER_LISTENERS_ibfk_1` FOREIGN KEY (`TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_adminPermissions`
--

DROP TABLE IF EXISTS `tb_adminPermissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_adminPermissions` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `adminUserID` int(10) unsigned NOT NULL,
  `communityID` int(10) unsigned NOT NULL,
  `adminPermissionsLabelID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `for_refund_tb_users`
--

DROP TABLE IF EXISTS `for_refund_tb_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `for_refund_tb_users` (
  `i` int(10) unsigned NOT NULL,
  `displayName` char(25) NOT NULL,
  `title` char(10) NOT NULL,
  `firstName` char(40) NOT NULL,
  `lastName` char(40) NOT NULL,
  `userName` char(50) NOT NULL,
  `subBalance` int(11) NOT NULL,
  `freeBalance` tinyint(3) unsigned DEFAULT NULL,
  `token` char(40) NOT NULL,
  `status` tinyint(3) unsigned NOT NULL,
  `deviceType` tinyint(3) unsigned NOT NULL,
  `device` char(40) NOT NULL,
  `userGroup` int(11) NOT NULL,
  `userType` int(10) unsigned NOT NULL DEFAULT '0',
  `lastDeviceLogin` int(10) unsigned NOT NULL,
  `lastWebLogin` int(10) unsigned NOT NULL,
  `nextSubPayment` int(10) unsigned NOT NULL,
  `lastPaymentTx` int(10) unsigned NOT NULL,
  `Address1` char(50) NOT NULL,
  `Address2` char(50) NOT NULL,
  `City` char(20) NOT NULL,
  `Postcode` char(15) NOT NULL,
  `Country` smallint(5) unsigned NOT NULL,
  `mobile` char(15) NOT NULL,
  `code` char(40) NOT NULL,
  `sessionID` char(40) NOT NULL,
  `ipAddress` char(40) NOT NULL,
  `tempToken` char(40) NOT NULL,
  `deviceString` char(100) NOT NULL,
  `canContact` tinyint(1) NOT NULL,
  `paymentType` varchar(255) DEFAULT 'UNKNOWN',
  `operator` int(10) unsigned NOT NULL,
  `pin` varchar(10) NOT NULL,
  `paymentEnabled` tinyint(1) NOT NULL DEFAULT '0',
  `paymentStatus` int(10) unsigned NOT NULL,
  `numPsmsRetries` int(10) unsigned NOT NULL,
  `age` tinyint(3) unsigned DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `facebookId` varchar(45) DEFAULT NULL,
  `lastSuccessfulPaymentTimeMillis` bigint(20) DEFAULT '0',
  `currentPaymentDetailsId` bigint(20) DEFAULT NULL,
  `deviceUID` varchar(255) DEFAULT NULL,
  `deviceModel` varchar(255) DEFAULT NULL,
  `potentialPromotion_i` int(11) DEFAULT NULL,
  `potentialPromoCodePromotion_i` int(11) DEFAULT NULL,
  `firstDeviceLoginMillis` bigint(20) DEFAULT NULL,
  `firstUserLoginMillis` bigint(20) DEFAULT NULL,
  `isFreeTrial` bit(1) NOT NULL DEFAULT b'0',
  `amountOfMoneyToUserNotification` decimal(5,2) NOT NULL DEFAULT '0.00',
  `lastSuccesfullPaymentSmsSendingTimestampMillis` bigint(20) NOT NULL DEFAULT '0',
  `freeTrialExpiredMillis` bigint(20) DEFAULT NULL,
  `freeTrialStartedTimestampMillis` bigint(20) DEFAULT NULL,
  `activation_status` varchar(255) DEFAULT NULL,
  `provider` varchar(255) DEFAULT NULL,
  `contract` varchar(255) DEFAULT NULL,
  `base64_encoded_app_store_receipt` longtext,
  `app_store_original_transaction_id` varchar(255) DEFAULT NULL,
  `last_subscribed_payment_system` varchar(255) DEFAULT NULL,
  `segment` char(255) DEFAULT NULL,
  `last_before48_sms_millis` bigint(20) DEFAULT '0',
  `tariff` char(255) NOT NULL DEFAULT '_3G',
  `video_free_trial_has_been_activated` tinyint(1) NOT NULL DEFAULT '0',
  `last_successful_payment_details_id` bigint(20) DEFAULT NULL,
  `contract_channel` varchar(255) DEFAULT 'DIRECT',
  `last_promo` int(10) DEFAULT NULL,
  `idfa` varchar(255) DEFAULT NULL,
  `recordCreationTimestamp` bigint(20) NOT NULL DEFAULT '0',
  `specialComment` char(50) DEFAULT NULL,
  PRIMARY KEY (`i`,`recordCreationTimestamp`),
  KEY `specialComment` (`specialComment`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `for_refund_tb_paymentDetails`
--

DROP TABLE IF EXISTS `for_refund_tb_paymentDetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `for_refund_tb_paymentDetails` (
  `paymentType` varchar(31) NOT NULL,
  `i` bigint(20) NOT NULL,
  `creationTimestampMillis` bigint(20) NOT NULL,
  `descriptionError` varchar(255) DEFAULT NULL,
  `disableTimestampMillis` bigint(20) NOT NULL,
  `lastPaymentStatus` varchar(255) DEFAULT NULL,
  `madeRetries` int(11) NOT NULL,
  `VPSTxId` varchar(255) DEFAULT NULL,
  `released` bit(1) DEFAULT NULL,
  `securityKey` varchar(255) DEFAULT NULL,
  `txAuthNo` varchar(255) DEFAULT NULL,
  `vendorTxCode` varchar(255) DEFAULT NULL,
  `paymentPolicyId` int(11) DEFAULT NULL,
  `retriesOnError` int(11) NOT NULL,
  `billingAgreementTxId` varchar(255) DEFAULT NULL,
  `migPhoneNumber` varchar(255) DEFAULT NULL,
  `activated` bit(1) NOT NULL DEFAULT b'1',
  `promotionPaymentPolicy_id` bigint(20) DEFAULT NULL,
  `owner_id` int(10) unsigned DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `errorCode` varchar(255) DEFAULT NULL,
  `last_failed_payment_notification_millis` bigint(20) DEFAULT NULL,
  `recordCreationTimestamp` bigint(20) NOT NULL DEFAULT '0',
  `specialComment` char(50) DEFAULT NULL,
  PRIMARY KEY (`i`,`recordCreationTimestamp`),
  KEY `specialComment` (`specialComment`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_operators`
--

DROP TABLE IF EXISTS `tb_operators`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_operators` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `migName` varchar(50) NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_communities`
--

DROP TABLE IF EXISTS `tb_communities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_communities` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` char(25) NOT NULL,
  `appVersion` tinyint(3) unsigned NOT NULL,
  `communityTypeID` int(10) unsigned NOT NULL,
  `displayName` varchar(100) NOT NULL,
  `assetName` varchar(100) NOT NULL,
  `rewriteURLParameter` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4CCCE1F6DF182BB1` (`appVersion`),
  CONSTRAINT `FK4CCCE1F6DF182BB1` FOREIGN KEY (`appVersion`) REFERENCES `tb_appVersions` (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_FIRED_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_FIRED_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_FIRED_TRIGGERS` (
  `ENTRY_ID` varchar(95) COLLATE utf8_unicode_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `IS_VOLATILE` varchar(1) COLLATE utf8_unicode_ci NOT NULL,
  `INSTANCE_NAME` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `STATE` varchar(16) COLLATE utf8_unicode_ci NOT NULL,
  `JOB_NAME` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `JOB_GROUP` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `IS_STATEFUL` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`ENTRY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_filter_tb_newsDetail`
--

DROP TABLE IF EXISTS `tb_filter_tb_newsDetail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_filter_tb_newsDetail` (
  `filters_id` tinyint(3) unsigned NOT NULL,
  `newDetails_i` int(11) NOT NULL,
  KEY `FK193A41FB8F3C13C` (`filters_id`),
  CONSTRAINT `FK193A41FB8F3C13C` FOREIGN KEY (`filters_id`) REFERENCES `tb_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_appVersions`
--

DROP TABLE IF EXISTS `tb_appVersions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_appVersions` (
  `i` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(25) NOT NULL,
  `description` char(50) NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_drmTypes`
--

DROP TABLE IF EXISTS `tb_drmTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_drmTypes` (
  `i` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(25) NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_promotionPaymentPolicy`
--

DROP TABLE IF EXISTS `tb_promotionPaymentPolicy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_promotionPaymentPolicy` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `promotion_i` int(11) DEFAULT NULL,
  `subcost` decimal(19,2) DEFAULT NULL,
  `subweeks` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2E3D2FE63BEC8B6D` (`promotion_i`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tt_fix`
--

DROP TABLE IF EXISTS `tt_fix`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tt_fix` (
  `useruid` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_mediaLogArchive`
--

DROP TABLE IF EXISTS `tb_mediaLogArchive`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_mediaLogArchive` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `userUID` int(10) unsigned NOT NULL,
  `mediaUID` int(10) unsigned NOT NULL,
  `logType` tinyint(3) unsigned NOT NULL,
  `logTimestamp` int(10) unsigned NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=21955846 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `community_charts`
--

DROP TABLE IF EXISTS `community_charts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `community_charts` (
  `chart_id` tinyint(4) NOT NULL,
  `community_id` int(11) NOT NULL,
  KEY `FK3410E96B4E1D2677` (`chart_id`),
  KEY `community_charts_PK_community_id` (`community_id`),
  CONSTRAINT `community_charts_U_community_id` FOREIGN KEY (`community_id`) REFERENCES `tb_communities` (`id`),
  CONSTRAINT `FK3410E96B4E1D2677` FOREIGN KEY (`chart_id`) REFERENCES `tb_charts` (`i`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_fileTypes`
--

DROP TABLE IF EXISTS `tb_fileTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_fileTypes` (
  `i` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(25) NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `promoted_devices`
--

DROP TABLE IF EXISTS `promoted_devices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `promoted_devices` (
  `deviceUID` varchar(255) NOT NULL,
  `community_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`deviceUID`),
  UNIQUE KEY `DEVICE_UID_INDEX` (`deviceUID`),
  KEY `offers_PK_community_id` (`community_id`),
  KEY `promoted_devices_PK_community_id` (`community_id`),
  CONSTRAINT `offers_U_community_id` FOREIGN KEY (`community_id`) REFERENCES `tb_communities` (`id`),
  CONSTRAINT `promoted_devices_U_community_id` FOREIGN KEY (`community_id`) REFERENCES `tb_communities` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_userTypes`
--

DROP TABLE IF EXISTS `tb_userTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_userTypes` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(15) NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_paymentTypes`
--

DROP TABLE IF EXISTS `tb_paymentTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_paymentTypes` (
  `i` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(15) NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_appTypes`
--

DROP TABLE IF EXISTS `tb_appTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_appTypes` (
  `i` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(20) NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_page_menus`
--

DROP TABLE IF EXISTS `tb_page_menus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_page_menus` (
  `i` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(30) NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ResourceFile`
--

DROP TABLE IF EXISTS `ResourceFile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ResourceFile` (
  `i` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` int(11) DEFAULT NULL,
  `path` varchar(255) NOT NULL,
  `MD5` varchar(255) DEFAULT NULL,
  `resolution` varchar(255) NOT NULL,
  `TrackId` bigint(20) DEFAULT NULL,
  `mediaHash` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=13765 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system`
--

DROP TABLE IF EXISTS `system`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `release_time_millis` bigint(20) NOT NULL,
  `version` char(8) NOT NULL,
  `release_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `version_index` (`version`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_chartUpdateDetail`
--

DROP TABLE IF EXISTS `tb_chartUpdateDetail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_chartUpdateDetail` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `chartUpdate` int(10) unsigned NOT NULL,
  `position` tinyint(3) unsigned NOT NULL,
  `media` int(10) unsigned NOT NULL,
  `prevPosition` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `chgPosition` tinyint(3) unsigned NOT NULL DEFAULT '3',
  `channel` varchar(255) DEFAULT NULL,
  `info` text,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_SIMPLE_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_SIMPLE_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_SIMPLE_TRIGGERS` (
  `TRIGGER_NAME` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(10) NOT NULL,
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `TRIGGER_NAME` (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `QRTZ_SIMPLE_TRIGGERS_ibfk_1` FOREIGN KEY (`TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_filter`
--

DROP TABLE IF EXISTS `tb_filter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_filter` (
  `id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `filterType` char(31) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `NowSubscribers`
--

DROP TABLE IF EXISTS `NowSubscribers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `NowSubscribers` (
  `i` int(10) unsigned NOT NULL DEFAULT '0',
  `currentPaymentDetailsId` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_newsUpdateDetail`
--

DROP TABLE IF EXISTS `tb_newsUpdateDetail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_newsUpdateDetail` (
  `i` int(5) unsigned NOT NULL AUTO_INCREMENT,
  `newsUpdate` int(10) unsigned NOT NULL,
  `position` tinyint(3) unsigned NOT NULL,
  `item` char(255) NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `subscription_campaign`
--

DROP TABLE IF EXISTS `subscription_campaign`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subscription_campaign` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(25) NOT NULL,
  `campaign_id` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `mobile-campaign_id` (`mobile`,`campaign_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1299 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_newsUpdates`
--

DROP TABLE IF EXISTS `tb_newsUpdates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_newsUpdates` (
  `i` int(5) unsigned NOT NULL AUTO_INCREMENT,
  `news` int(10) unsigned NOT NULL,
  `timestamp` int(10) unsigned NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_pendingPayments`
--

DROP TABLE IF EXISTS `tb_pendingPayments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_pendingPayments` (
  `i` bigint(20) NOT NULL AUTO_INCREMENT,
  `amount` decimal(19,2) DEFAULT NULL,
  `currencyISO` varchar(255) DEFAULT NULL,
  `internalTxId` varchar(255) DEFAULT NULL,
  `paymentSystem` varchar(255) DEFAULT NULL,
  `subweeks` int(11) NOT NULL,
  `timestamp` bigint(20) DEFAULT '0',
  `userId` int(11) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `expireTimeMillis` bigint(20) DEFAULT '0',
  `externalTxId` varchar(255) NOT NULL,
  `paymentDetailsId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`i`),
  KEY `FK999C3E13197A82E5` (`paymentDetailsId`),
  CONSTRAINT `FK999C3E13197A82E5` FOREIGN KEY (`paymentDetailsId`) REFERENCES `tb_paymentDetails` (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=1392617 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_artist`
--

DROP TABLE IF EXISTS `tb_artist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_artist` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(40) NOT NULL,
  `info` text,
  `realName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`i`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2901 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `messageBU`
--

DROP TABLE IF EXISTS `messageBU`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `messageBU` (
  `id` int(11) NOT NULL DEFAULT '0',
  `activated` bit(1) NOT NULL,
  `body` longtext CHARACTER SET utf8 NOT NULL,
  `community_id` tinyint(3) unsigned NOT NULL,
  `frequence` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `imageFileName` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `messageType` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `position` tinyint(4) NOT NULL,
  `publishTimeMillis` bigint(20) NOT NULL,
  `title` varchar(255) CHARACTER SET utf8 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_chartUpdates`
--

DROP TABLE IF EXISTS `tb_chartUpdates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_chartUpdates` (
  `i` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `chart` tinyint(3) unsigned NOT NULL,
  `timestamp` int(10) unsigned NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_apps`
--

DROP TABLE IF EXISTS `tb_apps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_apps` (
  `i` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `model` char(40) NOT NULL,
  `jad` char(255) DEFAULT NULL,
  `jar` char(40) NOT NULL,
  `appType` tinyint(3) unsigned NOT NULL,
  `communityID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_mediaLogTypes`
--

DROP TABLE IF EXISTS `tb_mediaLogTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_mediaLogTypes` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(20) NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_logs`
--

DROP TABLE IF EXISTS `user_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_logs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `last_update` bigint(20) DEFAULT NULL,
  `status` char(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `description` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `phoneNumber` char(25) COLLATE utf8_unicode_ci DEFAULT NULL,
  `type` char(25) COLLATE utf8_unicode_ci DEFAULT 'UPDATE_O2_USER',
  PRIMARY KEY (`id`),
  KEY `last_update` (`last_update`),
  KEY `user_id` (`user_id`),
  KEY `status` (`status`),
  KEY `description` (`description`),
  KEY `phoneNumber` (`phoneNumber`),
  KEY `type` (`type`),
  KEY `phoneNumber_type` (`phoneNumber`,`type`)
) ENGINE=InnoDB AUTO_INCREMENT=2362042 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `old_users`
--

DROP TABLE IF EXISTS `old_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `old_users` (
  `i` int(10) unsigned NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `temp_count`
--

DROP TABLE IF EXISTS `temp_count`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `temp_count` (
  `user` int(10) unsigned NOT NULL,
  `c` bigint(21) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_LOCKS`
--

DROP TABLE IF EXISTS `QRTZ_LOCKS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_LOCKS` (
  `LOCK_NAME` varchar(40) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_accountLog`
--

DROP TABLE IF EXISTS `tb_accountLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_accountLog` (
  `i` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `userUID` int(10) unsigned NOT NULL,
  `transactionType` int(11) NOT NULL,
  `balanceAfter` int(11) NOT NULL,
  `relatedMediaUID` int(10) unsigned DEFAULT NULL,
  `relatedPaymentUID` bigint(20) DEFAULT NULL,
  `logTimestamp` int(11) NOT NULL,
  `topupBalance` int(10) DEFAULT NULL,
  `amountUnused` int(10) DEFAULT NULL,
  `relatedTopup` int(10) DEFAULT NULL,
  `promoCode` varchar(255) DEFAULT NULL,
  `description` varchar(10000) DEFAULT NULL,
  PRIMARY KEY (`i`),
  KEY `user` (`userUID`)
) ENGINE=InnoDB AUTO_INCREMENT=15105248 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_support`
--

DROP TABLE IF EXISTS `tb_support`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_support` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user` int(10) unsigned NOT NULL,
  `subject` char(50) NOT NULL,
  `message` text,
  `status` tinyint(3) unsigned NOT NULL,
  `timestamp` int(10) unsigned NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `messages_filters`
--

DROP TABLE IF EXISTS `messages_filters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `messages_filters` (
  `messages_id` int(11) NOT NULL,
  `filterWithCtiteria_id` int(11) NOT NULL,
  PRIMARY KEY (`messages_id`,`filterWithCtiteria_id`),
  KEY `FKE99CCED0813E3712` (`messages_id`),
  KEY `FKE99CCED01B11A7A4` (`filterWithCtiteria_id`),
  CONSTRAINT `FKE99CCED01B11A7A4` FOREIGN KEY (`filterWithCtiteria_id`) REFERENCES `filters` (`id`),
  CONSTRAINT `FKE99CCED0813E3712` FOREIGN KEY (`messages_id`) REFERENCES `messages` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_paymentPolicy`
--

DROP TABLE IF EXISTS `tb_paymentPolicy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_paymentPolicy` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `communityID` int(10) unsigned NOT NULL,
  `subWeeks` tinyint(3) unsigned NOT NULL,
  `subCost` char(5) NOT NULL,
  `paymentType` varchar(50) NOT NULL,
  `operator` int(10) unsigned DEFAULT NULL,
  `shortCode` varchar(20) NOT NULL,
  `currencyIso` varchar(255) DEFAULT NULL,
  `availableInStore` bit(1) NOT NULL DEFAULT b'0',
  `app_store_product_id` varchar(255) DEFAULT NULL,
  `contract` char(255) DEFAULT NULL,
  `segment` char(255) DEFAULT NULL,
  `content_category` varchar(255) DEFAULT NULL,
  `content_type` varchar(255) DEFAULT NULL,
  `content_description` varchar(255) DEFAULT NULL,
  `sub_merchant_id` varchar(255) DEFAULT NULL,
  `provider` varchar(255) DEFAULT NULL,
  `tariff` char(255) NOT NULL DEFAULT '_3G',
  `media_type` char(255) NOT NULL DEFAULT 'AUDIO',
  `is_default` bit(1) DEFAULT b'0',
  `advanced_payment_seconds` int(10) unsigned NOT NULL,
  `after_next_sub_payment_seconds` int(10) unsigned NOT NULL,
  `online` bit(1) NOT NULL,
  PRIMARY KEY (`i`),
  KEY `FKBD4BAEC7BE165AAB` (`operator`),
  CONSTRAINT `FKBD4BAEC7BE165AAB` FOREIGN KEY (`operator`) REFERENCES `tb_operators` (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=135 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_BLOB_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_BLOB_TRIGGERS` (
  `TRIGGER_NAME` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `BLOB_DATA` blob,
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `TRIGGER_NAME` (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `QRTZ_BLOB_TRIGGERS_ibfk_1` FOREIGN KEY (`TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oldNowSubscribers`
--

DROP TABLE IF EXISTS `oldNowSubscribers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oldNowSubscribers` (
  `i` bigint(20) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_SCHEDULER_STATE`
--

DROP TABLE IF EXISTS `QRTZ_SCHEDULER_STATE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_SCHEDULER_STATE` (
  `INSTANCE_NAME` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  PRIMARY KEY (`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `google_plus_user_info`
--

DROP TABLE IF EXISTS `google_plus_user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `google_plus_user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `gp_id` varchar(100) DEFAULT NULL,
  `display_name` varchar(100) DEFAULT NULL,
  `picture_url` varchar(100) DEFAULT NULL,
  `date_of_birth` datetime DEFAULT NULL,
  `location` varchar(100) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `given_name` varchar(100) DEFAULT NULL,
  `family_name` varchar(100) DEFAULT NULL,
  `home_page` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `google_plus_user_info_id` FOREIGN KEY (`id`) REFERENCES `social_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QRTZ_CALENDARS`
--

DROP TABLE IF EXISTS `QRTZ_CALENDARS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_CALENDARS` (
  `CALENDAR_NAME` varchar(200) COLLATE utf8_unicode_ci NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_menus`
--

DROP TABLE IF EXISTS `tb_menus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_menus` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `description` varchar(40) NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `backup_device_user_data`
--

DROP TABLE IF EXISTS `backup_device_user_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `backup_device_user_data` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `community_url` varchar(255) NOT NULL,
  `user_id` int(11) NOT NULL,
  `xtify_token` char(255) NOT NULL,
  `device_uid` char(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `xtify_token` (`xtify_token`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2443165 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_systemLog`
--

DROP TABLE IF EXISTS `tb_systemLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_systemLog` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `timestamp` int(10) unsigned NOT NULL,
  `entry` text,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_promotions_tb_filter`
--

DROP TABLE IF EXISTS `tb_promotions_tb_filter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_promotions_tb_filter` (
  `tb_promotions_i` int(11) NOT NULL,
  `filters_id` tinyint(3) unsigned NOT NULL,
  KEY `FK23564B34CC35EB` (`tb_promotions_i`),
  KEY `FK23564BB8F3C13C` (`filters_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Track`
--

DROP TABLE IF EXISTS `Track`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Track` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `Ingestor` varchar(255) NOT NULL,
  `ISRC` varchar(255) NOT NULL,
  `Title` varchar(255) NOT NULL,
  `Artist` varchar(255) NOT NULL,
  `ProductId` varchar(255) DEFAULT NULL,
  `ProductCode` varchar(255) DEFAULT NULL,
  `Genre` varchar(255) DEFAULT NULL,
  `Copyright` varchar(255) DEFAULT NULL,
  `Year` varchar(255) DEFAULT NULL,
  `Album` varchar(255) DEFAULT NULL,
  `Info` varchar(255) DEFAULT NULL,
  `Xml` longblob,
  `IngestionDate` date NOT NULL,
  `IngestionUpdateDate` date DEFAULT NULL,
  `PublishDate` date DEFAULT NULL,
  `SubTitle` varchar(255) DEFAULT NULL,
  `Licensed` bit(1) DEFAULT NULL,
  `status` varchar(255) NOT NULL DEFAULT 'NONE',
  `resolution` varchar(255) NOT NULL DEFAULT 'RATE_ORIGINAL',
  `itunesUrl` varchar(255) DEFAULT NULL,
  `explicit` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reactivation_user_info`
--

DROP TABLE IF EXISTS `reactivation_user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reactivation_user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned DEFAULT NULL,
  `reactivation_request` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `reactivation_user_info_user_id_uq` (`user_id`),
  CONSTRAINT `reactivation_user_info_user_id` FOREIGN KEY (`user_id`) REFERENCES `tb_users` (`i`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1308 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_users_that_will_be_send_on_reactivation`
--

DROP TABLE IF EXISTS `tb_users_that_will_be_send_on_reactivation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_users_that_will_be_send_on_reactivation` (
  `i` int(10) unsigned NOT NULL,
  `displayName` char(25) NOT NULL,
  `title` char(10) NOT NULL,
  `firstName` char(40) NOT NULL,
  `lastName` char(40) NOT NULL,
  `userName` char(50) NOT NULL,
  `subBalance` int(11) NOT NULL,
  `freeBalance` tinyint(3) unsigned DEFAULT NULL,
  `token` char(40) NOT NULL,
  `status` tinyint(3) unsigned NOT NULL,
  `deviceType` tinyint(3) unsigned NOT NULL,
  `device` char(40) NOT NULL,
  `userGroup` int(11) NOT NULL,
  `userType` int(10) unsigned NOT NULL DEFAULT '0',
  `lastDeviceLogin` int(10) unsigned NOT NULL,
  `lastWebLogin` int(10) unsigned NOT NULL,
  `nextSubPayment` int(10) unsigned NOT NULL,
  `lastPaymentTx` int(10) unsigned NOT NULL,
  `Address1` char(50) NOT NULL,
  `Address2` char(50) NOT NULL,
  `City` char(20) NOT NULL,
  `Postcode` char(15) NOT NULL,
  `Country` smallint(5) unsigned NOT NULL,
  `mobile` char(15) NOT NULL,
  `code` char(40) NOT NULL,
  `sessionID` char(40) NOT NULL,
  `ipAddress` char(40) NOT NULL,
  `tempToken` char(40) NOT NULL,
  `deviceString` char(100) NOT NULL,
  `canContact` tinyint(4) NOT NULL,
  `paymentType` varchar(255) DEFAULT 'UNKNOWN',
  `operator` int(10) unsigned NOT NULL,
  `pin` varchar(10) NOT NULL,
  `paymentEnabled` tinyint(4) NOT NULL DEFAULT '0',
  `paymentStatus` int(10) unsigned NOT NULL,
  `numPsmsRetries` int(10) unsigned NOT NULL,
  `age` tinyint(3) unsigned DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `facebookId` varchar(45) DEFAULT NULL,
  `lastSuccessfulPaymentTimeMillis` bigint(20) DEFAULT '0',
  `currentPaymentDetailsId` bigint(20) DEFAULT NULL,
  `deviceUID` varchar(255) DEFAULT NULL,
  `deviceModel` varchar(255) DEFAULT NULL,
  `potentialPromotion_i` int(11) DEFAULT NULL,
  `potentialPromoCodePromotion_i` int(11) DEFAULT NULL,
  `firstDeviceLoginMillis` bigint(20) DEFAULT NULL,
  `firstUserLoginMillis` bigint(20) DEFAULT NULL,
  `isFreeTrial` bit(1) NOT NULL DEFAULT b'0',
  `amountOfMoneyToUserNotification` decimal(5,2) NOT NULL DEFAULT '0.00',
  `lastSuccesfullPaymentSmsSendingTimestampMillis` bigint(20) NOT NULL DEFAULT '0',
  `freeTrialExpiredMillis` bigint(20) DEFAULT NULL,
  `freeTrialStartedTimestampMillis` bigint(20) DEFAULT NULL,
  `activation_status` varchar(255) DEFAULT NULL,
  `provider` varchar(255) DEFAULT NULL,
  `contract` varchar(255) DEFAULT NULL,
  `base64_encoded_app_store_receipt` longtext,
  `app_store_original_transaction_id` varchar(255) DEFAULT NULL,
  `last_subscribed_payment_system` varchar(255) DEFAULT NULL,
  `segment` char(255) DEFAULT NULL,
  `last_before48_sms_millis` bigint(20) DEFAULT '0',
  `tariff` char(255) NOT NULL DEFAULT '_3G',
  `video_free_trial_has_been_activated` tinyint(4) NOT NULL DEFAULT '0',
  `last_successful_payment_details_id` bigint(20) DEFAULT NULL,
  `contract_channel` varchar(255) DEFAULT 'DIRECT',
  `last_promo` int(11) DEFAULT NULL,
  `idfa` varchar(255) DEFAULT NULL,
  `scriptTimestampSeconds` bigint(20) NOT NULL,
  `recordCreationTimestampSeconds` bigint(20) NOT NULL,
  `specialComment` char(50) DEFAULT NULL,
  PRIMARY KEY (`i`,`recordCreationTimestampSeconds`),
  KEY `deviceUID` (`deviceUID`,`userGroup`),
  KEY `userName` (`userName`,`userGroup`),
  KEY `facebookId` (`facebookId`),
  KEY `FKFAEDF4F753C7738F` (`potentialPromoCodePromotion_i`),
  KEY `lastDeviceLogin` (`lastDeviceLogin`),
  KEY `mobile` (`mobile`),
  KEY `tb_users_PK_last_successful_payment_details` (`last_successful_payment_details_id`),
  KEY `tb_users_PK_potentialPromoCodePromotion_i` (`potentialPromoCodePromotion_i`),
  KEY `tb_users_PK_userGroup` (`userGroup`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_news`
--

DROP TABLE IF EXISTS `tb_news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_news` (
  `i` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(25) NOT NULL,
  `numEntries` tinyint(3) unsigned NOT NULL,
  `community` int(11) DEFAULT NULL,
  `timestamp` int(11) NOT NULL,
  PRIMARY KEY (`i`),
  KEY `tb_news_PK_community` (`community`),
  CONSTRAINT `tb_news_U_community` FOREIGN KEY (`community`) REFERENCES `tb_communities` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_labels`
--

DROP TABLE IF EXISTS `tb_labels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_labels` (
  `i` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(30) NOT NULL,
  PRIMARY KEY (`i`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_media`
--

DROP TABLE IF EXISTS `tb_media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_media` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `isrc` char(15) NOT NULL,
  `title` char(50) NOT NULL,
  `artist` int(10) unsigned NOT NULL,
  `audioFile` int(10) unsigned NOT NULL,
  `headerFile` int(10) unsigned DEFAULT NULL,
  `imageFileSmall` int(10) unsigned NOT NULL,
  `imageFIleLarge` int(10) unsigned NOT NULL,
  `info` text,
  `genre` int(10) unsigned NOT NULL DEFAULT '0',
  `label` tinyint(3) unsigned NOT NULL,
  `price` decimal(5,2) DEFAULT NULL,
  `price_currency` char(4) DEFAULT NULL,
  `imgFileResolution` int(10) unsigned DEFAULT NULL,
  `purchasedFile` int(10) unsigned DEFAULT NULL,
  `audioPreviewFile` int(10) unsigned DEFAULT NULL,
  `headerPreviewFile` int(10) unsigned DEFAULT NULL,
  `iTunesUrl` varchar(255) DEFAULT NULL,
  `publishDate` int(10) unsigned NOT NULL DEFAULT '0',
  `type` int(10) DEFAULT NULL,
  `trackId` bigint(20) DEFAULT NULL,
  `amazonUrl` varchar(255) DEFAULT NULL,
  `areArtistUrls` tinyint(1) NOT NULL,
  PRIMARY KEY (`i`),
  UNIQUE KEY `isrc` (`isrc`),
  KEY `FKFA76D6D3FE903C83` (`genre`),
  KEY `FKFA76D6D333F43406` (`headerFile`),
  KEY `FKFA76D6D3C94D20CF` (`audioFile`),
  KEY `FKFA76D6D36F6581FC` (`purchasedFile`),
  KEY `FKFA76D6D3C070C4B1` (`artist`),
  KEY `FKFA76D6D35F12E22B` (`audioPreviewFile`),
  KEY `FKFA76D6D3D555EAC1` (`imageFIleLarge`),
  KEY `FKFA76D6D3D5BDC48D` (`imageFileSmall`),
  KEY `FKFA76D6D376067428` (`imgFileResolution`),
  KEY `FKFA76D6D32411314` (`headerPreviewFile`)
) ENGINE=InnoDB AUTO_INCREMENT=3047498 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `messages` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `activated` bit(1) NOT NULL,
  `body` longtext NOT NULL,
  `community_id` int(11) DEFAULT NULL,
  `frequence` varchar(255) DEFAULT NULL,
  `imageFileName` varchar(255) DEFAULT NULL,
  `messageType` varchar(255) DEFAULT NULL,
  `position` int(11) NOT NULL,
  `publishTimeMillis` bigint(20) NOT NULL,
  `title` varchar(255) NOT NULL,
  `actionType` varchar(255) DEFAULT NULL,
  `action` varchar(255) DEFAULT NULL,
  `actionButtonText` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `position` (`position`,`community_id`,`messageType`,`publishTimeMillis`),
  KEY `messages_PK_community_id` (`community_id`),
  CONSTRAINT `messages_U_community_id` FOREIGN KEY (`community_id`) REFERENCES `tb_communities` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20004 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_chartDetail`
--

DROP TABLE IF EXISTS `tb_chartDetail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_chartDetail` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `chart` tinyint(4) NOT NULL,
  `position` tinyint(3) unsigned NOT NULL,
  `media` int(10) unsigned DEFAULT NULL,
  `prevPosition` tinyint(4) DEFAULT NULL,
  `chgPosition` int(11) DEFAULT NULL,
  `channel` varchar(255) DEFAULT NULL,
  `info` text,
  `publishTimeMillis` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  `image_filename` varchar(255) DEFAULT NULL,
  `image_title` varchar(255) DEFAULT NULL,
  `subtitle` char(50) DEFAULT NULL,
  `title` char(50) DEFAULT NULL,
  `locked` bit(1) DEFAULT b'0',
  `defaultChart` bit(1) DEFAULT b'0',
  PRIMARY KEY (`i`),
  UNIQUE KEY `tb_chartDetail_U_3_1` (`media`,`chart`,`publishTimeMillis`),
  KEY `tb_chartdetail_PK_chart` (`chart`),
  KEY `tb_chartdetail_PK_media` (`media`),
  CONSTRAINT `tb_chartdetail_U_chart` FOREIGN KEY (`chart`) REFERENCES `tb_charts` (`i`),
  CONSTRAINT `tb_chartdetail_U_media` FOREIGN KEY (`media`) REFERENCES `tb_media` (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=150967 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_accountLogTypes`
--

DROP TABLE IF EXISTS `tb_accountLogTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_accountLogTypes` (
  `i` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(40) DEFAULT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_genres`
--

DROP TABLE IF EXISTS `tb_genres`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_genres` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` char(25) NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `device_user_data`
--

DROP TABLE IF EXISTS `device_user_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `device_user_data` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `community_url` varchar(255) NOT NULL,
  `user_id` int(11) NOT NULL,
  `xtify_token` char(255) NOT NULL,
  `device_uid` char(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `xtify_token` (`xtify_token`),
  UNIQUE KEY `userID_deviceUID` (`user_id`,`device_uid`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2479032 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_admin_pages`
--

DROP TABLE IF EXISTS `tb_admin_pages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_admin_pages` (
  `i` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(40) NOT NULL,
  `title` varchar(40) NOT NULL,
  `accessLevel` tinyint(4) NOT NULL DEFAULT '0',
  `mainMenu` smallint(5) unsigned NOT NULL DEFAULT '0',
  `subMenu` smallint(5) unsigned NOT NULL DEFAULT '0',
  `userMenu` smallint(5) unsigned NOT NULL DEFAULT '0',
  `contentModule` smallint(5) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`i`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `facebook_user_info`
--

DROP TABLE IF EXISTS `facebook_user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `facebook_user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `first_name` varchar(100) DEFAULT NULL,
  `surname` varchar(100) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `country` varchar(100) DEFAULT NULL,
  `profile_url` varchar(200) DEFAULT NULL,
  `fb_id` varchar(100) DEFAULT NULL,
  `user_name` varchar(100) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `date_of_birth` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `facebook_user_info_id` FOREIGN KEY (`id`) REFERENCES `social_info` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=720907 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `for_refund_user_charts`
--

DROP TABLE IF EXISTS `for_refund_user_charts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `for_refund_user_charts` (
  `user_id` int(10) unsigned NOT NULL,
  `chart_id` tinyint(4) NOT NULL,
  `recordCreationTimestamp` bigint(20) DEFAULT NULL,
  `specialComment` char(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  KEY `specialComment` (`specialComment`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `topupFix`
--

DROP TABLE IF EXISTS `topupFix`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `topupFix` (
  `useruid` int(10) unsigned NOT NULL,
  `from_unixtime(logtimestamp)` datetime DEFAULT NULL,
  `topupBalance` int(10) DEFAULT NULL,
  `subweeks` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_users_that_should_be_in_activated_status`
--

DROP TABLE IF EXISTS `tb_users_that_should_be_in_activated_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_users_that_should_be_in_activated_status` (
  `i` int(10) unsigned NOT NULL,
  `displayName` char(25) COLLATE utf8_unicode_ci NOT NULL,
  `title` char(10) COLLATE utf8_unicode_ci NOT NULL,
  `firstName` char(40) COLLATE utf8_unicode_ci NOT NULL,
  `lastName` char(40) COLLATE utf8_unicode_ci NOT NULL,
  `userName` char(50) COLLATE utf8_unicode_ci NOT NULL,
  `subBalance` int(11) NOT NULL,
  `freeBalance` tinyint(3) unsigned DEFAULT NULL,
  `token` char(40) COLLATE utf8_unicode_ci NOT NULL,
  `status` tinyint(3) unsigned NOT NULL,
  `deviceType` tinyint(3) unsigned NOT NULL,
  `device` char(40) COLLATE utf8_unicode_ci NOT NULL,
  `userGroup` int(11) NOT NULL,
  `userType` int(10) unsigned NOT NULL DEFAULT '0',
  `lastDeviceLogin` int(10) unsigned NOT NULL,
  `lastWebLogin` int(10) unsigned NOT NULL,
  `nextSubPayment` int(10) unsigned NOT NULL,
  `lastPaymentTx` int(10) unsigned NOT NULL,
  `Address1` char(50) COLLATE utf8_unicode_ci NOT NULL,
  `Address2` char(50) COLLATE utf8_unicode_ci NOT NULL,
  `City` char(20) COLLATE utf8_unicode_ci NOT NULL,
  `Postcode` char(15) COLLATE utf8_unicode_ci NOT NULL,
  `Country` smallint(5) unsigned NOT NULL,
  `mobile` char(15) COLLATE utf8_unicode_ci NOT NULL,
  `code` char(40) COLLATE utf8_unicode_ci NOT NULL,
  `sessionID` char(40) COLLATE utf8_unicode_ci NOT NULL,
  `ipAddress` char(40) COLLATE utf8_unicode_ci NOT NULL,
  `tempToken` char(40) COLLATE utf8_unicode_ci NOT NULL,
  `deviceString` char(100) COLLATE utf8_unicode_ci NOT NULL,
  `canContact` tinyint(4) NOT NULL,
  `paymentType` varchar(255) COLLATE utf8_unicode_ci DEFAULT 'UNKNOWN',
  `operator` int(10) unsigned NOT NULL,
  `pin` varchar(10) COLLATE utf8_unicode_ci NOT NULL,
  `paymentEnabled` tinyint(4) NOT NULL DEFAULT '0',
  `paymentStatus` int(10) unsigned NOT NULL,
  `numPsmsRetries` int(10) unsigned NOT NULL,
  `age` tinyint(3) unsigned DEFAULT NULL,
  `gender` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `facebookId` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `lastSuccessfulPaymentTimeMillis` bigint(20) DEFAULT '0',
  `currentPaymentDetailsId` bigint(20) DEFAULT NULL,
  `deviceUID` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `deviceModel` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `potentialPromotion_i` int(11) DEFAULT NULL,
  `potentialPromoCodePromotion_i` int(11) DEFAULT NULL,
  `firstDeviceLoginMillis` bigint(20) DEFAULT NULL,
  `firstUserLoginMillis` bigint(20) DEFAULT NULL,
  `isFreeTrial` bit(1) NOT NULL DEFAULT b'0',
  `amountOfMoneyToUserNotification` decimal(5,2) NOT NULL DEFAULT '0.00',
  `lastSuccesfullPaymentSmsSendingTimestampMillis` bigint(20) NOT NULL DEFAULT '0',
  `freeTrialExpiredMillis` bigint(20) DEFAULT NULL,
  `freeTrialStartedTimestampMillis` bigint(20) DEFAULT NULL,
  `activation_status` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `provider` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `contract` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `base64_encoded_app_store_receipt` longtext COLLATE utf8_unicode_ci,
  `app_store_original_transaction_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `last_subscribed_payment_system` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `segment` char(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `last_before48_sms_millis` bigint(20) DEFAULT '0',
  `tariff` char(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '_3G',
  `video_free_trial_has_been_activated` tinyint(4) NOT NULL DEFAULT '0',
  `last_successful_payment_details_id` bigint(20) DEFAULT NULL,
  `contract_channel` varchar(255) COLLATE utf8_unicode_ci DEFAULT 'DIRECT',
  `last_promo` int(11) DEFAULT NULL,
  `idfa` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `scriptTimestampSeconds` bigint(20) NOT NULL,
  `recordCreationTimestampSeconds` bigint(20) NOT NULL,
  `specialComment` char(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`i`,`recordCreationTimestampSeconds`),
  KEY `deviceUID` (`deviceUID`,`userGroup`),
  KEY `userName` (`userName`,`userGroup`),
  KEY `facebookId` (`facebookId`),
  KEY `FKFAEDF4F753C7738F` (`potentialPromoCodePromotion_i`),
  KEY `lastDeviceLogin` (`lastDeviceLogin`),
  KEY `mobile` (`mobile`),
  KEY `tb_users_PK_last_successful_payment_details` (`last_successful_payment_details_id`),
  KEY `tb_users_PK_potentialPromoCodePromotion_i` (`potentialPromoCodePromotion_i`),
  KEY `tb_users_PK_userGroup` (`userGroup`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tb_chgPosition`
--

DROP TABLE IF EXISTS `tb_chgPosition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tb_chgPosition` (
  `i` tinyint(3) unsigned NOT NULL,
  `label` char(10) NOT NULL,
  PRIMARY KEY (`i`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `DropContent`
--

DROP TABLE IF EXISTS `DropContent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DropContent` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ISRC` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `Artist` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `Title` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `Updated` bit(1) NOT NULL,
  `DropId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKAD936A6A68072A61` (`DropId`),
  CONSTRAINT `FKAD936A6A68072A61` FOREIGN KEY (`DropId`) REFERENCES `IngestionLog` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `IngestionLog`
--

DROP TABLE IF EXISTS `IngestionLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `IngestionLog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `Ingestor` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `IngestionDate` datetime NOT NULL,
  `Status` bit(1) NOT NULL,
  `DropName` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `Message` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'cn_service'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-06-09 17:25:16
