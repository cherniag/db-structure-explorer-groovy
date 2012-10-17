--
-- Table structure for table `tb_accountLog`
--

DROP TABLE IF EXISTS `tb_accountLog`;

CREATE TABLE `tb_accountLog` (
  `i` bigint(20) NOT NULL AUTO_INCREMENT,
  `balanceAfter` int(11) NOT NULL,
  `logTimestamp` int(11) NOT NULL,
  `promoCode` varchar(255) DEFAULT NULL,
  `relatedMediaUID` int(11) NOT NULL,
  `relatedPaymentUID` bigint(20) NOT NULL,
  `transactionType` tinyint(4) NOT NULL,
  `userUID` int(11) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_accountLogTypes`
--

DROP TABLE IF EXISTS `tb_accountLogTypes`;

CREATE TABLE `tb_accountLogTypes` (
  `i` tinyint(4) NOT NULL AUTO_INCREMENT,
  `name` char(40) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_adminPermissions`
--

DROP TABLE IF EXISTS `tb_adminPermissions`;

CREATE TABLE `tb_adminPermissions` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `adminPermissionsLabelID` int(11) NOT NULL,
  `adminUserID` int(11) NOT NULL,
  `communityID` int(11) NOT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_adminPermissionsLabel`
--

DROP TABLE IF EXISTS `tb_adminPermissionsLabel`;

CREATE TABLE `tb_adminPermissionsLabel` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_adminUserType`
--

DROP TABLE IF EXISTS `tb_adminUserType`;

CREATE TABLE `tb_adminUserType` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_adminUsers`
--

DROP TABLE IF EXISTS `tb_adminUsers`;

CREATE TABLE `tb_adminUsers` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `accessLevel` tinyint(4) NOT NULL,
  `adminUserTypeID` int(11) NOT NULL,
  `firstName` varchar(255) DEFAULT NULL,
  `ipAddress` varchar(255) DEFAULT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  `lastUse` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `sessionID` varchar(255) DEFAULT NULL,
  `userID` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_admin_pages`
--

DROP TABLE IF EXISTS `tb_admin_pages`;

CREATE TABLE `tb_admin_pages` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `accessLevel` tinyint(4) NOT NULL,
  `contentModule` smallint(5) unsigned DEFAULT NULL,
  `mainMenu` smallint(5) unsigned DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `subMenu` smallint(5) unsigned DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `userMenu` smallint(5) unsigned DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_appTypes`
--

DROP TABLE IF EXISTS `tb_appTypes`;

CREATE TABLE `tb_appTypes` (
  `i` tinyint(4) NOT NULL AUTO_INCREMENT,
  `name` char(20) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_appVersionCountry`
--

DROP TABLE IF EXISTS `tb_appVersionCountry`;

CREATE TABLE `tb_appVersionCountry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `appVersion_id` tinyint(3) unsigned NOT NULL,
  `country_id` smallint(6) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
);

--
-- Table structure for table `tb_appVersions`
--

DROP TABLE IF EXISTS `tb_appVersions`;

CREATE TABLE `tb_appVersions` (
  `i` tinyint(4) NOT NULL AUTO_INCREMENT,
  `description` char(50) DEFAULT NULL,
  `name` char(25) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_apps`
--

DROP TABLE IF EXISTS `tb_apps`;

CREATE TABLE `tb_apps` (
  `i` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `appType` tinyint(4) NOT NULL,
  `jad` char(40) DEFAULT NULL,
  `jar` char(40) DEFAULT NULL,
  `model` char(40) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_artist`
--

DROP TABLE IF EXISTS `tb_artist`;

CREATE TABLE `tb_artist` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `info` text,
  `name` char(40) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_chartDetail`
--

DROP TABLE IF EXISTS `tb_chartDetail`;

create table `tb_chartDetail`
(
   `i` INT UNSIGNED not null auto_increment,
   `chart` INT UNSIGNED not null,
   `position` TINYINT UNSIGNED not null,
   `media` INT UNSIGNED not null,
   `prevPosition` TINYINT UNSIGNED default '0' not null,
   `chgPosition` TINYINT UNSIGNED default '3' not null,
   primary key (`i`)
)
;

--
-- Table structure for table `tb_chartUpdateDetail`
--

DROP TABLE IF EXISTS `tb_chartUpdateDetail`;

CREATE TABLE `tb_chartUpdateDetail` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `chartUpdate` int(11) NOT NULL,
  `media` int(11) NOT NULL,
  `position` tinyint(4) NOT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_chartUpdates`
--

DROP TABLE IF EXISTS `tb_chartUpdates`;

CREATE TABLE `tb_chartUpdates` (
  `i` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `chart` tinyint(4) NOT NULL,
  `timestamp` int(11) NOT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_charts`
--

DROP TABLE IF EXISTS `tb_charts`;

CREATE TABLE `tb_charts` (
  `i` tinyint(4) NOT NULL AUTO_INCREMENT,
  `community` tinyint(4) NOT NULL,
  `numTracks` tinyint(4) NOT NULL,
  `timestamp` int(11) NOT NULL,
  `name` char(25) DEFAULT NULL,
  `genre` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_chgPosition`
--

DROP TABLE IF EXISTS `tb_chgPosition`;

create table `tb_chgPosition`
(
   `i` TINYINT UNSIGNED not null, `label` CHAR(10) not null, primary key (`i`)
)
;

--
-- Table structure for table `tb_communities`
--

DROP TABLE IF EXISTS `tb_communities`;

CREATE TABLE `tb_communities` (
  `i` tinyint(4) NOT NULL AUTO_INCREMENT,
  `appVersion` tinyint(4) NOT NULL,
  `assetName` varchar(255) DEFAULT NULL,
  `communityTypeID` int(11) NOT NULL,
  `displayName` varchar(255) DEFAULT NULL,
  `name` char(25) DEFAULT NULL,
  `rewriteURLParameter` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_country`
--

DROP TABLE IF EXISTS `tb_country`;

CREATE TABLE `tb_country` (
  `i` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `fullName` varchar(255) DEFAULT NULL,
  `name` char(10) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_deviceTypes`
--

DROP TABLE IF EXISTS `tb_deviceTypes`;

CREATE TABLE `tb_deviceTypes` (
  `i` tinyint(4) NOT NULL AUTO_INCREMENT,
  `name` char(25) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_drm`
--

DROP TABLE IF EXISTS `tb_drm`;

CREATE TABLE `tb_drm` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `drmValue` tinyint(4) NOT NULL,
  `media` int(11) DEFAULT NULL,
  `timestamp` int(11) NOT NULL,
  `user` int(11) NOT NULL,
  `drmType` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_drmPolicy`
--

DROP TABLE IF EXISTS `tb_drmPolicy`;

CREATE TABLE `tb_drmPolicy` (
  `i` tinyint(4) NOT NULL AUTO_INCREMENT,
  `community` tinyint(4) NOT NULL,
  `drmType` tinyint(4) NOT NULL,
  `drmValue` tinyint(4) NOT NULL,
  `name` char(25) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_drmTypes`
--

DROP TABLE IF EXISTS `tb_drmTypes`;

CREATE TABLE `tb_drmTypes` (
  `i` tinyint(4) NOT NULL AUTO_INCREMENT,
  `name` char(25) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_fileTypes`
--

DROP TABLE IF EXISTS `tb_fileTypes`;

CREATE TABLE `tb_fileTypes` (
  `i` tinyint(4) NOT NULL AUTO_INCREMENT,
  `name` char(25) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_files`
--

DROP TABLE IF EXISTS `tb_files`;

CREATE TABLE `tb_files` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `fileType` tinyint(4) NOT NULL,
  `filename` char(40) DEFAULT NULL,
  `size` int(11) NOT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_genres`
--

DROP TABLE IF EXISTS `tb_genres`;

CREATE TABLE `tb_genres` (
  `i` tinyint(4) NOT NULL AUTO_INCREMENT,
  `name` char(25) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_labels`
--

DROP TABLE IF EXISTS `tb_labels`;

CREATE TABLE `tb_labels` (
  `i` tinyint(4) NOT NULL AUTO_INCREMENT,
  `name` char(30) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_media`
--

DROP TABLE IF EXISTS `tb_media`;

CREATE TABLE `tb_media` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `iTunesUrl` varchar(255) DEFAULT NULL,
  `info` text,
  `isrc` char(15) DEFAULT NULL,
  `label` tinyint(4) NOT NULL,
  `price` decimal(5,2) DEFAULT NULL,
  `price_currency` char(4) DEFAULT NULL,
  `title` char(50) DEFAULT NULL,
  `artist` int(11) DEFAULT NULL,
  `audioFile` int(11) DEFAULT NULL,
  `audioPreviewFile` int(11) DEFAULT NULL,
  `genre` tinyint(4) DEFAULT NULL,
  `headerFile` int(11) DEFAULT NULL,
  `headerPreviewFile` int(11) DEFAULT NULL,
  `imageFIleLarge` int(11) DEFAULT NULL,
  `imageFileSmall` int(11) DEFAULT NULL,
  `imgFileResolution` int(11) DEFAULT NULL,
  `purchasedFile` int(11) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_mediaLog`
--

DROP TABLE IF EXISTS `tb_mediaLog`;

CREATE TABLE `tb_mediaLog` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `logTimestamp` int(11) NOT NULL,
  `userUID` int(11) NOT NULL,
  `logType` tinyint(4) NOT NULL,
  `mediaUID` int(11) NOT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_mediaLogTypes`
--

DROP TABLE IF EXISTS `tb_mediaLogTypes`;

CREATE TABLE `tb_mediaLogTypes` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `name` char(20) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_menuItems`
--

DROP TABLE IF EXISTS `tb_menuItems`;

CREATE TABLE `tb_menuItems` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `internalLink` smallint(5) unsigned DEFAULT NULL,
  `internalParams` varchar(255) DEFAULT NULL,
  `label` varchar(255) DEFAULT NULL,
  `menu` smallint(5) unsigned DEFAULT NULL,
  `position` tinyint(4) NOT NULL,
  `submenu` smallint(5) unsigned DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_menus`
--

DROP TABLE IF EXISTS `tb_menus`;

CREATE TABLE `tb_menus` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_news`
--

DROP TABLE IF EXISTS `tb_news`;

CREATE TABLE `tb_news` (
  `i` tinyint(4) NOT NULL AUTO_INCREMENT,
  `community` tinyint(4) NOT NULL,
  `name` char(25) DEFAULT NULL,
  `numEntries` tinyint(4) NOT NULL,
  `timestamp` int(11) NOT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_newsDetail`
--

DROP TABLE IF EXISTS `tb_newsDetail`;

CREATE TABLE `tb_newsDetail` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `item` char(255) DEFAULT NULL,
  `position` tinyint(4) NOT NULL,
  `news` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_newsUpdateDetail`
--

DROP TABLE IF EXISTS `tb_newsUpdateDetail`;

CREATE TABLE `tb_newsUpdateDetail` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `item` char(255) DEFAULT NULL,
  `newsUpdate` int(11) NOT NULL,
  `position` tinyint(4) NOT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_newsUpdates`
--

DROP TABLE IF EXISTS `tb_newsUpdates`;

CREATE TABLE `tb_newsUpdates` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `news` int(11) NOT NULL,
  `timestamp` int(11) NOT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_operators`
--

DROP TABLE IF EXISTS `tb_operators`;

CREATE TABLE `tb_operators` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `migName` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_page_menu_items`
--

DROP TABLE IF EXISTS `tb_page_menu_items`;

CREATE TABLE `tb_page_menu_items` (
  `i` tinyint(4) NOT NULL AUTO_INCREMENT,
  `internalLink` smallint(5) unsigned DEFAULT NULL,
  `internalParams` char(40) DEFAULT NULL,
  `label` char(40) DEFAULT NULL,
  `menu` tinyint(4) NOT NULL,
  `position` tinyint(4) NOT NULL,
  `submenu` tinyint(4) NOT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_page_menus`
--

DROP TABLE IF EXISTS `tb_page_menus`;

CREATE TABLE `tb_page_menus` (
  `i` tinyint(4) NOT NULL AUTO_INCREMENT,
  `name` char(30) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_pages`
--

DROP TABLE IF EXISTS `tb_pages`;

CREATE TABLE `tb_pages` (
  `i` tinyint(4) NOT NULL AUTO_INCREMENT,
  `accessLevel` tinyint(4) NOT NULL,
  `contentModule` smallint(5) unsigned DEFAULT NULL,
  `mainMenu` smallint(5) unsigned DEFAULT NULL,
  `name` char(40) DEFAULT NULL,
  `subMenu` smallint(5) unsigned DEFAULT NULL,
  `title` char(40) DEFAULT NULL,
  `userMenu` smallint(5) unsigned DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_paymentDetails`
--

DROP TABLE IF EXISTS `tb_paymentDetails`;

CREATE TABLE `tb_paymentDetails` (
  `paymentType` varchar(31) NOT NULL,
  `i` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationTimestampMillis` bigint(20) NOT NULL,
  `descriptionError` varchar(255) DEFAULT NULL,
  `disableTimestampMillis` bigint(20) NOT NULL,
  `lastPaymentStatus` varchar(255) DEFAULT NULL,
  `madeRetries` int(11) NOT NULL,
  `retriesOnError` int(11) NOT NULL,
  `VPSTxId` varchar(255) DEFAULT NULL,
  `released` bit(1) DEFAULT NULL,
  `securityKey` varchar(255) DEFAULT NULL,
  `txAuthNo` varchar(255) DEFAULT NULL,
  `vendorTxCode` varchar(255) DEFAULT NULL,
  `billingAgreementTxId` varchar(255) DEFAULT NULL,
  `migPhoneNumber` varchar(255) DEFAULT NULL,
  `activated` bit(1) NOT NULL DEFAULT true,
  `paymentPolicyId` smallint(6) DEFAULT NULL,
  `userId` int(11) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_paymentPolicy`
--

DROP TABLE IF EXISTS `tb_paymentPolicy`;

CREATE TABLE `tb_paymentPolicy` (
  `i` smallint(6) NOT NULL AUTO_INCREMENT,
  `communityID` int(11) NOT NULL,
  `currencyIso` varchar(255) DEFAULT NULL,
  `operator` int(11) NOT NULL,
  `paymentType` varchar(255) DEFAULT NULL,
  `shortCode` varchar(255) DEFAULT NULL,
  `subCost` char(5) NOT NULL,
  `subWeeks` tinyint(3) NOT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_paymentStatus`
--

DROP TABLE IF EXISTS `tb_paymentStatus`;

CREATE TABLE `tb_paymentStatus` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

--
-- Table structure for table `tb_paymentTypes`
--

DROP TABLE IF EXISTS `tb_paymentTypes`;

CREATE TABLE `tb_paymentTypes` (
  `i` tinyint(4) NOT NULL AUTO_INCREMENT,
  `name` char(15) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_payments`
--

DROP TABLE IF EXISTS `tb_payments`;

CREATE TABLE `tb_payments` (
  `paymentType` varchar(31) NOT NULL,
  `i` bigint(20) NOT NULL AUTO_INCREMENT,
  `amount` float NOT NULL,
  `currencyCode` varchar(255) DEFAULT NULL,
  `description` char(100) DEFAULT NULL,
  `externalAuthCode` char(20) DEFAULT NULL,
  `externalSecurityKey` char(20) DEFAULT NULL,
  `externalTxCode` char(38) DEFAULT NULL,
  `internalTxCode` char(40) DEFAULT NULL,
  `numPaymentRetries` int(11) DEFAULT NULL,
  `relatedPayment` bigint(20) NOT NULL,
  `status` char(15) DEFAULT NULL,
  `statusDetail` char(255) DEFAULT NULL,
  `subWeeks` tinyint(3) NOT NULL,
  `timestamp` int(11) NOT NULL,
  `txType` int(11) NOT NULL,
  `userUID` int(11) NOT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_pendingPayments`
--

DROP TABLE IF EXISTS `tb_pendingPayments`;

CREATE TABLE `tb_pendingPayments` (
  `i` bigint(20) NOT NULL AUTO_INCREMENT,
  `amount` decimal(19,2) DEFAULT NULL,
  `currencyISO` varchar(255) DEFAULT NULL,
  `internalTxId` varchar(255) DEFAULT NULL,
  `paymentSystem` varchar(255) DEFAULT NULL,
  `subweeks` int(11) NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `timestamp` bigint(20) DEFAULT 0,
  `expireTimeMillis` bigint(20) DEFAULT 0,
  `userId` int(11) DEFAULT NULL,
  PRIMARY KEY (`i`)
);


--
-- Table structure for table `tb_submittedPayments`
--

DROP TABLE IF EXISTS `tb_submittedPayments`;

CREATE TABLE `tb_submittedPayments` (
  `i` bigint(20) NOT NULL AUTO_INCREMENT,
  `amount` decimal(19,2) DEFAULT NULL,
  `currencyISO` varchar(255) DEFAULT NULL,
  `internalTxId` varchar(255) DEFAULT NULL,
  `paymentSystem` varchar(255) DEFAULT NULL,
  `subweeks` int(11) NOT NULL,
  `timestamp` bigint(20) DEFAULT 0,
  `status` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `descriptionError` varchar(255) DEFAULT NULL,
  `userId` int(11) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_promoCode`
--

DROP TABLE IF EXISTS `tb_promoCode`;

CREATE TABLE `tb_promoCode` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `promotionId` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

--
-- Table structure for table `tb_promotions`
--

DROP TABLE IF EXISTS `tb_promotions`;

CREATE TABLE `tb_promotions` (
  `i` tinyint(4) NOT NULL AUTO_INCREMENT,
  `description` char(40) DEFAULT NULL,
  `endDate` int(11) NOT NULL,
  `freeWeeks` tinyint(4) NOT NULL,
  `isActive` bit(1) NOT NULL,
  `maxUsers` smallint(5) unsigned DEFAULT NULL,
  `numUsers` smallint(5) unsigned DEFAULT NULL,
  `showPromotion` bit(1) NOT NULL,
  `startDate` int(11) NOT NULL,
  `subWeeks` tinyint(4) NOT NULL,
  `type` char(20) DEFAULT NULL,
  `userGroup` tinyint(4) NOT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_support`
--

DROP TABLE IF EXISTS `tb_support`;

CREATE TABLE `tb_support` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `message` text,
  `status` tinyint(4) NOT NULL,
  `subject` char(50) DEFAULT NULL,
  `timestamp` int(11) NOT NULL,
  `user` int(11) NOT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_systemLog`
--

DROP TABLE IF EXISTS `tb_systemLog`;

CREATE TABLE `tb_systemLog` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `entry` text,
  `timestamp` int(11) NOT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_userGroups`
--

DROP TABLE IF EXISTS `tb_userGroups`;

CREATE TABLE `tb_userGroups` (
  `i` tinyint(4) NOT NULL AUTO_INCREMENT,
  `chart` tinyint(4) NOT NULL,
  `community` tinyint(4) NOT NULL,
  `drmPolicy` tinyint(4) NOT NULL,
  `name` char(25) DEFAULT NULL,
  `news` tinyint(4) NOT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_userStatus`
--

DROP TABLE IF EXISTS `tb_userStatus`;

CREATE TABLE `tb_userStatus` (
  `i` tinyint(4) NOT NULL AUTO_INCREMENT,
  `name` char(25) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_userTypes`
--

DROP TABLE IF EXISTS `tb_userTypes`;

CREATE TABLE `tb_userTypes` (
  `i` tinyint(4) NOT NULL AUTO_INCREMENT,
  `name` char(25) DEFAULT NULL,
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_users`
--

DROP TABLE IF EXISTS `tb_users`;

CREATE TABLE `tb_users` (
  `i` int(11) NOT NULL AUTO_INCREMENT,
  `address1` char(50) DEFAULT NULL,
  `address2` char(50) DEFAULT NULL,
  `canContact` bit(1) NOT NULL,
  `city` char(20) DEFAULT NULL,
  `code` char(40) DEFAULT NULL,
  `country` smallint(5) unsigned DEFAULT NULL,
  `device` char(40) DEFAULT NULL,
  `deviceString` char(100) DEFAULT NULL,
  `deviceType` tinyint(4) NOT NULL,
  `displayName` char(25) DEFAULT NULL,
  `firstName` char(40) DEFAULT NULL,
  `freeBalance` tinyint(4) NOT NULL,
  `ipAddress` char(40) DEFAULT NULL,
  `lastDeviceLogin` int(11) NOT NULL,
  `lastName` char(40) DEFAULT NULL,
  `lastPaymentTx` int(11) NOT NULL,
  `lastWebLogin` int(11) NOT NULL,
  `mobile` char(15) DEFAULT NULL,
  `nextSubPayment` int(11) NOT NULL,
  `numPsmsRetries` int(11) NOT NULL,
  `operator` int(11) NOT NULL,
  `paymentEnabled` bit(1) NOT NULL,
  `paymentStatus` int(11) NOT NULL,
  `paymentType` varchar(255) DEFAULT NULL,
  `pin` varchar(255) DEFAULT NULL,
  `postcode` char(15) DEFAULT NULL,
  `sessionID` char(40) DEFAULT NULL,
  `status` tinyint(4) NOT NULL,
  `subBalance` int(11) NOT NULL,
  `tempToken` char(40) DEFAULT NULL,
  `title` char(10) DEFAULT NULL,
  `token` char(40) DEFAULT NULL,
  `userGroup` tinyint(4) NOT NULL,
  `userName` char(40) DEFAULT NULL,
  `userType` tinyint(4) NOT NULL,
  `currentPaymentDetailsId` bigint(20) DEFAULT NULL,
  `lastSuccessfulPaymentTimeMillis` bigint(20) DEFAULT NULL,
  `facebookId` VARCHAR(45),
  PRIMARY KEY (`i`)
);

--
-- Table structure for table `tb_useriPhoneDetails`
--

DROP TABLE IF EXISTS `tb_useriPhoneDetails`;

create table `tb_useriPhoneDetails`
(
   `i` INT not null auto_increment,
   `userUID` INT,
   `token` VARCHAR(64),
   `usergroup` INT,
   `nbUpdates` INT,
   `status` INT,
   primary key (`i`)
)
;