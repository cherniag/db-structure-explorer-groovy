-- MySQL dump 10.11
--
-- Host: localhost    Database: cn_service
-- ------------------------------------------------------
-- Server version	5.0.77

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
-- Table structure for table `tb_accountLog`
--

DROP TABLE IF EXISTS `tb_accountLog`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_accountLog` (
  `i` bigint(20) unsigned NOT NULL auto_increment,
  `userUID` int(10) unsigned NOT NULL,
  `transactionType` tinyint(3) unsigned NOT NULL,
  `balanceAfter` tinyint(3) unsigned NOT NULL,
  `relatedMediaUID` int(11) NOT NULL,
  `relatedPaymentUID` bigint(20) NOT NULL,
  `logTimestamp` int(11) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=349 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_accountLogTypes`
--

DROP TABLE IF EXISTS `tb_accountLogTypes`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_accountLogTypes` (
  `i` tinyint(3) unsigned NOT NULL auto_increment,
  `name` char(20) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_adminPermissions`
--

DROP TABLE IF EXISTS `tb_adminPermissions`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_adminPermissions` (
  `i` int(10) unsigned NOT NULL auto_increment,
  `adminUserID` int(10) unsigned NOT NULL,
  `communityID` int(10) unsigned NOT NULL,
  `adminPermissionsLabelID` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_adminPermissionsLabel`
--

DROP TABLE IF EXISTS `tb_adminPermissionsLabel`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_adminPermissionsLabel` (
  `i` int(10) unsigned NOT NULL auto_increment,
  `description` varchar(50) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_adminUserType`
--

DROP TABLE IF EXISTS `tb_adminUserType`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_adminUserType` (
  `i` int(10) unsigned NOT NULL auto_increment,
  `description` varchar(50) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_adminUsers`
--

DROP TABLE IF EXISTS `tb_adminUsers`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_adminUsers` (
  `i` int(10) unsigned NOT NULL auto_increment,
  `firstName` varchar(40) NOT NULL,
  `lastName` varchar(40) NOT NULL,
  `userID` varchar(40) NOT NULL,
  `accessLevel` tinyint(4) NOT NULL default '0',
  `adminUserTypeID` int(10) unsigned NOT NULL default '100',
  `password` varchar(40) NOT NULL,
  `sessionID` varchar(40) NOT NULL,
  `lastUse` varchar(40) NOT NULL,
  `ipAddress` varchar(40) NOT NULL,
  PRIMARY KEY  (`i`),
  UNIQUE KEY `userID` (`userID`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_admin_pages`
--

DROP TABLE IF EXISTS `tb_admin_pages`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_admin_pages` (
  `i` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(40) NOT NULL,
  `title` varchar(40) NOT NULL,
  `accessLevel` tinyint(4) NOT NULL default '0',
  `mainMenu` smallint(5) unsigned NOT NULL default '0',
  `subMenu` smallint(5) unsigned NOT NULL default '0',
  `userMenu` smallint(5) unsigned NOT NULL default '0',
  `contentModule` smallint(5) unsigned NOT NULL default '0',
  PRIMARY KEY  (`i`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_appTypes`
--

DROP TABLE IF EXISTS `tb_appTypes`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_appTypes` (
  `i` tinyint(3) unsigned NOT NULL auto_increment,
  `name` char(20) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_appVersionCountry`
--

DROP TABLE IF EXISTS `tb_appVersionCountry`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_appVersionCountry` (
  `appVersion_id` tinyint(3) unsigned NOT NULL,
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `country_id` smallint(5) unsigned NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_appVersions`
--

DROP TABLE IF EXISTS `tb_appVersions`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_appVersions` (
  `i` tinyint(3) unsigned NOT NULL auto_increment,
  `name` char(25) NOT NULL,
  `description` char(50) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_apps`
--

DROP TABLE IF EXISTS `tb_apps`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_apps` (
  `i` smallint(5) unsigned NOT NULL auto_increment,
  `model` char(40) NOT NULL,
  `jad` char(40) NOT NULL,
  `jar` char(40) NOT NULL,
  `appType` tinyint(3) unsigned NOT NULL,
  `communityID` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_artist`
--

DROP TABLE IF EXISTS `tb_artist`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_artist` (
  `i` int(10) unsigned NOT NULL auto_increment,
  `name` char(40) NOT NULL,
  `info` text NOT NULL,
  PRIMARY KEY  (`i`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=172 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_chartDetail`
--

DROP TABLE IF EXISTS `tb_chartDetail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_chartDetail` (
  `i` int(10) unsigned NOT NULL auto_increment,
  `chart` int(10) unsigned NOT NULL,
  `position` tinyint(3) unsigned NOT NULL,
  `media` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=123 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_chartUpdateDetail`
--

DROP TABLE IF EXISTS `tb_chartUpdateDetail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_chartUpdateDetail` (
  `i` int(10) unsigned NOT NULL auto_increment,
  `chartUpdate` int(10) unsigned NOT NULL,
  `position` tinyint(3) unsigned NOT NULL,
  `media` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=21 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_chartUpdates`
--

DROP TABLE IF EXISTS `tb_chartUpdates`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_chartUpdates` (
  `i` smallint(5) unsigned NOT NULL auto_increment,
  `chart` tinyint(3) unsigned NOT NULL,
  `timestamp` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_charts`
--

DROP TABLE IF EXISTS `tb_charts`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_charts` (
  `i` tinyint(3) unsigned NOT NULL auto_increment,
  `name` char(25) NOT NULL,
  `numTracks` tinyint(3) unsigned NOT NULL,
  `community` tinyint(3) unsigned NOT NULL,
  `genre` tinyint(3) unsigned NOT NULL,
  `timestamp` int(11) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_communities`
--

DROP TABLE IF EXISTS `tb_communities`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_communities` (
  `i` tinyint(3) unsigned NOT NULL auto_increment,
  `name` char(25) NOT NULL,
  `appVersion` tinyint(3) unsigned NOT NULL,
  `communityTypeID` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_country`
--

DROP TABLE IF EXISTS `tb_country`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_country` (
  `i` smallint(5) unsigned NOT NULL auto_increment,
  `name` char(10) NOT NULL,
  `fullName` varchar(200) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_deviceTypes`
--

DROP TABLE IF EXISTS `tb_deviceTypes`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_deviceTypes` (
  `i` tinyint(3) unsigned NOT NULL auto_increment,
  `name` char(25) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_drm`
--

DROP TABLE IF EXISTS `tb_drm`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_drm` (
  `i` int(10) unsigned NOT NULL auto_increment,
  `user` int(10) unsigned NOT NULL,
  `media` int(10) unsigned NOT NULL,
  `drmType` tinyint(3) unsigned NOT NULL,
  `drmValue` tinyint(3) unsigned NOT NULL,
  `timestamp` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=3497 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_drmPolicy`
--

DROP TABLE IF EXISTS `tb_drmPolicy`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_drmPolicy` (
  `i` tinyint(3) unsigned NOT NULL auto_increment,
  `name` char(25) NOT NULL,
  `drmType` tinyint(3) unsigned NOT NULL,
  `drmValue` tinyint(3) unsigned NOT NULL,
  `community` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_drmTypes`
--

DROP TABLE IF EXISTS `tb_drmTypes`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_drmTypes` (
  `i` tinyint(3) unsigned NOT NULL auto_increment,
  `name` char(25) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_fileTypes`
--

DROP TABLE IF EXISTS `tb_fileTypes`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_fileTypes` (
  `i` tinyint(3) unsigned NOT NULL auto_increment,
  `name` char(25) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_files`
--

DROP TABLE IF EXISTS `tb_files`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_files` (
  `i` int(10) unsigned NOT NULL auto_increment,
  `filename` char(40) NOT NULL,
  `size` int(10) unsigned NOT NULL,
  `fileType` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY  (`i`),
  UNIQUE KEY `filename` (`filename`)
) ENGINE=MyISAM AUTO_INCREMENT=2883 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_genres`
--

DROP TABLE IF EXISTS `tb_genres`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_genres` (
  `i` tinyint(3) unsigned NOT NULL auto_increment,
  `name` char(25) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_labels`
--

DROP TABLE IF EXISTS `tb_labels`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_labels` (
  `i` tinyint(3) unsigned NOT NULL auto_increment,
  `name` char(30) NOT NULL,
  PRIMARY KEY  (`i`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=60 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_media`
--

DROP TABLE IF EXISTS `tb_media`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_media` (
  `i` int(10) unsigned NOT NULL auto_increment,
  `isrc` char(15) NOT NULL,
  `title` char(50) NOT NULL,
  `artist` int(10) unsigned NOT NULL,
  `audioFile` int(10) unsigned NOT NULL,
  `headerFile` int(10) unsigned NOT NULL,
  `imageFileSmall` int(10) unsigned NOT NULL,
  `imageFIleLarge` int(10) unsigned NOT NULL,
  `info` text NOT NULL,
  `genre` tinyint(4) NOT NULL,
  `label` tinyint(3) unsigned NOT NULL,
  `price` decimal(5,2) default NULL,
  `price_currency` char(4) default NULL,
  `imgFileResolution` int(10) unsigned default NULL,
  `purchasedFile` int(10) unsigned default NULL,
  PRIMARY KEY  (`i`),
  UNIQUE KEY `isrc` (`isrc`)
) ENGINE=MyISAM AUTO_INCREMENT=190 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_mediaLog`
--

DROP TABLE IF EXISTS `tb_mediaLog`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_mediaLog` (
  `i` int(10) unsigned NOT NULL auto_increment,
  `userUID` int(10) unsigned NOT NULL,
  `mediaUID` int(10) unsigned NOT NULL,
  `logType` tinyint(3) unsigned NOT NULL,
  `logTimestamp` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=32429 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_mediaLogTypes`
--

DROP TABLE IF EXISTS `tb_mediaLogTypes`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_mediaLogTypes` (
  `i` int(10) unsigned NOT NULL auto_increment,
  `name` char(20) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_menuItems`
--

DROP TABLE IF EXISTS `tb_menuItems`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_menuItems` (
  `i` int(10) unsigned NOT NULL auto_increment,
  `label` varchar(40) NOT NULL,
  `menu` smallint(5) unsigned NOT NULL,
  `internalLink` smallint(5) unsigned NOT NULL default '0',
  `internalParams` varchar(40) NOT NULL,
  `submenu` smallint(5) unsigned NOT NULL default '0',
  `position` tinyint(3) unsigned NOT NULL default '0',
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=17 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_menus`
--

DROP TABLE IF EXISTS `tb_menus`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_menus` (
  `i` int(10) unsigned NOT NULL auto_increment,
  `description` varchar(40) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_news`
--

DROP TABLE IF EXISTS `tb_news`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_news` (
  `i` tinyint(3) unsigned NOT NULL auto_increment,
  `name` char(25) NOT NULL,
  `numEntries` tinyint(3) unsigned NOT NULL,
  `community` tinyint(3) unsigned NOT NULL,
  `timestamp` int(11) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_newsDetail`
--

DROP TABLE IF EXISTS `tb_newsDetail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_newsDetail` (
  `i` int(10) unsigned NOT NULL auto_increment,
  `news` int(10) unsigned NOT NULL,
  `position` tinyint(3) unsigned NOT NULL,
  `item` char(255) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=41 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_newsUpdateDetail`
--

DROP TABLE IF EXISTS `tb_newsUpdateDetail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_newsUpdateDetail` (
  `i` int(5) unsigned NOT NULL auto_increment,
  `newsUpdate` int(10) unsigned NOT NULL,
  `position` tinyint(3) unsigned NOT NULL,
  `item` char(255) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=31 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_newsUpdates`
--

DROP TABLE IF EXISTS `tb_newsUpdates`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_newsUpdates` (
  `i` int(5) unsigned NOT NULL auto_increment,
  `news` int(10) unsigned NOT NULL,
  `timestamp` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_page_menu_items`
--

DROP TABLE IF EXISTS `tb_page_menu_items`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_page_menu_items` (
  `i` tinyint(3) unsigned NOT NULL auto_increment,
  `menu` tinyint(3) unsigned NOT NULL,
  `label` char(40) NOT NULL,
  `internalLink` smallint(5) unsigned NOT NULL,
  `internalParams` char(40) NOT NULL,
  `submenu` tinyint(3) unsigned NOT NULL,
  `position` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_page_menus`
--

DROP TABLE IF EXISTS `tb_page_menus`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_page_menus` (
  `i` tinyint(3) unsigned NOT NULL auto_increment,
  `name` char(30) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_pages`
--

DROP TABLE IF EXISTS `tb_pages`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_pages` (
  `i` tinyint(3) unsigned NOT NULL auto_increment,
  `name` char(40) NOT NULL,
  `title` char(40) NOT NULL,
  `accessLevel` tinyint(3) unsigned NOT NULL,
  `mainMenu` smallint(5) unsigned NOT NULL,
  `subMenu` smallint(5) unsigned NOT NULL,
  `userMenu` smallint(5) unsigned NOT NULL,
  `contentModule` smallint(5) unsigned NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_paymentPolicy`
--

DROP TABLE IF EXISTS `tb_paymentPolicy`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_paymentPolicy` (
  `i` smallint(5) unsigned NOT NULL auto_increment,
  `communityID` int(10) unsigned NOT NULL,
  `subWeeks` tinyint(3) unsigned NOT NULL,
  `subCost` char(5) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_paymentTypes`
--

DROP TABLE IF EXISTS `tb_paymentTypes`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_paymentTypes` (
  `i` tinyint(3) unsigned NOT NULL auto_increment,
  `name` char(15) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_payments`
--

DROP TABLE IF EXISTS `tb_payments`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_payments` (
  `i` bigint(20) unsigned NOT NULL auto_increment,
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
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=2904 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_promotions`
--

DROP TABLE IF EXISTS `tb_promotions`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_promotions` (
  `i` tinyint(3) unsigned NOT NULL auto_increment,
  `description` char(40) NOT NULL,
  `numUsers` smallint(5) unsigned NOT NULL,
  `maxUsers` smallint(5) unsigned NOT NULL,
  `startDate` int(10) unsigned NOT NULL,
  `endDate` int(10) unsigned NOT NULL,
  `isActive` tinyint(1) NOT NULL,
  `freeWeeks` tinyint(3) unsigned NOT NULL,
  `subWeeks` tinyint(3) unsigned NOT NULL,
  `userGroup` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_support`
--

DROP TABLE IF EXISTS `tb_support`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_support` (
  `i` int(10) unsigned NOT NULL auto_increment,
  `user` int(10) unsigned NOT NULL,
  `subject` char(50) NOT NULL,
  `message` text NOT NULL,
  `status` tinyint(3) unsigned NOT NULL,
  `timestamp` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_systemLog`
--

DROP TABLE IF EXISTS `tb_systemLog`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_systemLog` (
  `i` int(10) unsigned NOT NULL auto_increment,
  `timestamp` int(10) unsigned NOT NULL,
  `entry` text NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_userGroups`
--

DROP TABLE IF EXISTS `tb_userGroups`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_userGroups` (
  `i` tinyint(3) unsigned NOT NULL auto_increment,
  `name` char(25) NOT NULL,
  `community` tinyint(3) unsigned NOT NULL,
  `chart` tinyint(3) unsigned NOT NULL,
  `news` tinyint(3) unsigned NOT NULL,
  `drmPolicy` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_userStatus`
--

DROP TABLE IF EXISTS `tb_userStatus`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_userStatus` (
  `i` tinyint(3) unsigned NOT NULL auto_increment,
  `name` char(25) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_userTypes`
--

DROP TABLE IF EXISTS `tb_userTypes`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_userTypes` (
  `i` tinyint(3) unsigned NOT NULL auto_increment,
  `name` char(15) NOT NULL,
  PRIMARY KEY  (`i`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `tb_users`
--

DROP TABLE IF EXISTS `tb_users`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tb_users` (
  `i` int(10) unsigned NOT NULL auto_increment,
  `displayName` char(25) NOT NULL,
  `title` char(10) NOT NULL,
  `firstName` char(40) NOT NULL,
  `lastName` char(40) NOT NULL,
  `userName` char(50) NOT NULL,
  `subBalance` tinyint(3) unsigned NOT NULL,
  `freeBalance` tinyint(3) unsigned NOT NULL,
  `token` char(40) NOT NULL,
  `status` tinyint(3) unsigned NOT NULL,
  `deviceType` tinyint(3) unsigned NOT NULL,
  `device` char(40) NOT NULL,
  `userGroup` tinyint(3) unsigned NOT NULL,
  `userType` tinyint(3) unsigned NOT NULL,
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
  PRIMARY KEY  (`i`),
  UNIQUE KEY `userName` (`userName`,`userGroup`)
) ENGINE=MyISAM AUTO_INCREMENT=127 DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-08-11  9:48:58
