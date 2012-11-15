-- LEGACY_START
drop table tb_apps;

CREATE TABLE IF NOT EXISTS `tb_apps` (
  `i` smallint(5) unsigned NOT NULL auto_increment,
  `model` char(40) NOT NULL,
  `jad` char(40) NOT NULL,
  `jar` char(40) NOT NULL,
  `appType` tinyint(3) unsigned NOT NULL,
  `communityID` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`i`)
) DEFAULT CHARSET=latin1 AUTO_INCREMENT=9;

INSERT INTO `tb_apps` ( `model`, `jad`, `jar`, `appType`, `communityID`) VALUES
('a8181'	  ,'chartsnow_CN.apk'	,''		   ,1,1),
('Nexus S'	  ,'chartsnow_CN.apk'	,''		   ,1,1),
('Wildfire S'	  ,'chartsnow_CN.apk'	,''		   ,1,1),
('S510e'	  ,'chartsnow_CN.apk'	,''		   ,1,1),
('Desire HD'	  ,'chartsnow_CN.apk'	,''		   ,1,1),
('GT-I9100'	  ,'chartsnow_CN.apk'	,''		   ,1,1),
('BlackBerry 8520','CN_ChartsNow.jad'	,'CN_ChartsNow.cod',2,1),
('BlackBerry 9300','CN_ChartsNow.jad'	,'CN_ChartsNow.cod',2,1),
('BlackBerry 9700','CN_ChartsNow.jad'	,'CN_ChartsNow.cod',2,1),
('BlackBerry 9780','CN_ChartsNow.jad'	,'CN_ChartsNow.cod',2,1),

('a8181'	  ,'chartsnow_CN.apk'	,''		   ,1,2),
('Nexus S'	  ,'chartsnow_CN.apk'	,''		   ,1,2),
('Wildfire S'	  ,'chartsnow_CN.apk'	,''		   ,1,2),
('S510e'	  ,'chartsnow_CN.apk'	,''		   ,1,2),
('Desire HD'	  ,'chartsnow_CN.apk'	,''		   ,1,2),
('GT-I9100'	  ,'chartsnow_CN.apk'	,''		   ,1,2),
('BlackBerry 8520','CN_ChartsNow.jad'	,'CN_ChartsNow.cod',2,2),
('BlackBerry 9300','CN_ChartsNow.jad'	,'CN_ChartsNow.cod',2,2),
('BlackBerry 9700','CN_ChartsNow.jad'	,'CN_ChartsNow.cod',2,2),
('BlackBerry 9780','CN_ChartsNow.jad'	,'CN_ChartsNow.cod',2,2),

('a8181'	  ,'chartsnow_CN.apk'	,''		   ,1,3),
('Nexus S'	  ,'chartsnow_CN.apk'	,''		   ,1,3),
('Wildfire S'	  ,'chartsnow_CN.apk'	,''		   ,1,3),
('S510e'	  ,'chartsnow_CN.apk'	,''		   ,1,3),
('Desire HD'	  ,'chartsnow_CN.apk'	,''		   ,1,3),
('GT-I9100'	  ,'chartsnow_CN.apk'	,''		   ,1,3),
('BlackBerry 8520','CN_ChartsNow.jad'	,'CN_ChartsNow.cod',2,3),
('BlackBerry 9300','CN_ChartsNow.jad'	,'CN_ChartsNow.cod',2,3),
('BlackBerry 9700','CN_ChartsNow.jad'	,'CN_ChartsNow.cod',2,3),
('BlackBerry 9780','CN_ChartsNow.jad'	,'CN_ChartsNow.cod',2,3),

('a8181'	  ,'metalhammer_CN.apk'	,''		   ,1,4),
('Nexus S'	  ,'metalhammer_CN.apk' ,''		   ,1,4),
('Wildfire S'	  ,'metalhammer_CN.apk'	,''		   ,1,4),
('S510e'	  ,'metalhammer_CN.apk'	,''		   ,1,4),
('Desire HD'	  ,'metalhammer_CN.apk'	,''		   ,1,4),
('GT-I9100'	  ,'metalhammer_CN.apk'	,''		   ,1,4),
('BlackBerry 8520','CN_MetalHammer.jad'	,'CN_MetalHammer.cod',2,4),
('BlackBerry 9300','CN_MetalHammer.jad'	,'CN_MetalHammer.cod',2,4),
('BlackBerry 9700','CN_MetalHammer.jad'	,'CN_MetalHammer.cod',2,4),
('BlackBerry 9780','CN_MetalHammer.jad'	,'CN_MetalHammer.cod',2,4);


/*
insert into tb_communities (name, appVersion, communityTypeID) values ('Metal Hammer', 1, 4);
insert into tb_drmPolicy (name, drmType, drmValue, community) values ('Default Policy', 1, 100, 4);
insert into tb_charts (name, numTracks, community, genre, timestamp) values ('The Heavy List', 25, 4, 1, now());
insert into tb_news (name, numEntries, community, timestamp) values ('The Heavy List', 10, 4, now());
insert into tb_userGroups (name, community, chart, news, drmPolicy) values ('Default Group', 4, 4, 4, 5);

insert into tb_newsDetail (news, position) values (4,1);
insert into tb_newsDetail (news, position) values (4,2);
insert into tb_newsDetail (news, position) values (4,3);
insert into tb_newsDetail (news, position) values (4,4);
insert into tb_newsDetail (news, position) values (4,5);
insert into tb_newsDetail (news, position) values (4,6);
insert into tb_newsDetail (news, position) values (4,7);
insert into tb_newsDetail (news, position) values (4,8);
insert into tb_newsDetail (news, position) values (4,9);
insert into tb_newsDetail (news, position) values (4,10);

insert into tb_chartDetail (chart, position) values (4,1);
insert into tb_chartDetail (chart, position) values (4,2);
insert into tb_chartDetail (chart, position) values (4,3);
insert into tb_chartDetail (chart, position) values (4,4);
insert into tb_chartDetail (chart, position) values (4,5);
insert into tb_chartDetail (chart, position) values (4,6);
insert into tb_chartDetail (chart, position) values (4,7);
insert into tb_chartDetail (chart, position) values (4,8);
insert into tb_chartDetail (chart, position) values (4,9);
insert into tb_chartDetail (chart, position) values (4,10);
insert into tb_chartDetail (chart, position) values (4,11);
insert into tb_chartDetail (chart, position) values (4,12);
insert into tb_chartDetail (chart, position) values (4,13);
insert into tb_chartDetail (chart, position) values (4,14);
insert into tb_chartDetail (chart, position) values (4,15);
insert into tb_chartDetail (chart, position) values (4,16);
insert into tb_chartDetail (chart, position) values (4,17);
insert into tb_chartDetail (chart, position) values (4,18);
insert into tb_chartDetail (chart, position) values (4,19);
insert into tb_chartDetail (chart, position) values (4,20);
insert into tb_chartDetail (chart, position) values (4,21);
insert into tb_chartDetail (chart, position) values (4,22);
insert into tb_chartDetail (chart, position) values (4,23);
insert into tb_chartDetail (chart, position) values (4,24);
insert into tb_chartDetail (chart, position) values (4,25);
*/

CREATE TABLE IF NOT EXISTS `tb_paymentPolicy` (
  `i` smallint(5) unsigned NOT NULL auto_increment,
  `communityID` int(10) unsigned NOT NULL,
  `subWeeks` tinyint(3) unsigned NOT NULL,
  `subCost` char(5) NOT NULL,
  PRIMARY KEY  (`i`)
) DEFAULT CHARSET=latin1 AUTO_INCREMENT=4;

INSERT INTO `tb_paymentPolicy` (`communityID`, `subWeeks`, `subCost`) VALUES
( 1, 4, '5.00'),
( 2, 4, '5.00'),
( 3, 5, '5.00'),
( 4, 4, '5.00');


-- LEGACY_END
-- -------------------------------------------------------------------------------------------------

-- CL-1043: Implement BUY TRACK
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-1043
alter table tb_media add price decimal(5,2), ADD price_currency char(4);
update tb_media set price=1,price_currency='WEEK';
-- ----------------------------------------------------------------
-- CL-1218: Add registering user country vs APP_VERSION check when registering user and if fail send correct error to client
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-1218
alter table tb_country add column fullName varchar(200);
update tb_country set fullName='Great Britain' where i=1;
-- maybe it was bad for old API 
alter table tb_country MODIFY fullName varchar(200) NOT NULL;

create table tb_appVersionCountry(appVersion_id TINYINT UNSIGNED not null,id BIGINT UNSIGNED not null auto_increment,country_id SMALLINT UNSIGNED not null,primary key (id));
create unique index `id` on `tb_appVersionCountry`(`id`);
insert into tb_appVersionCountry (appVersion_id,country_id) values(1,1);
-- ----------------------------------------------------------------
-- CL-1224: Make modifications to GET_FILE command, use details from specification
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-1224
alter table tb_media add column imgFileResolution INT(10) UNSIGNED;
-- maybe it was bad for old API if imgFileResolution will be not null
alter table tb_media add column purchasedFile INT(10) UNSIGNED;
-- maybe it was bad for old API if purchasedFile will be not null
-- -----------------------------------------------------------------
-- CL-1222: Create download track page with purchased  and not downloaded tracks list and download button
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-1222
insert into tb_mediaLogTypes (tb_mediaLogTypes.name) values('DOWNLOAD_ORIGINAL');

delete from tb_users where userName = '888@888.com';
delete from tb_users where userName = 'ma9@cn.mobi';
alter table tb_users ADD UNIQUE (userName,userGroup);

insert into tb_paymentTypes (i,name) values (7,'RELEASE');
insert into tb_paymentTypes (i,name) values (8,'DEFERRED');

UPDATE tb_paymentPolicy SET subWeeks=4, subCost='4.62' WHERE i=4;
INSERT INTO tb_promotions (i,description,numUsers,maxUsers,startDate,endDate,isActive,freeWeeks,subWeeks,userGroup)
VALUES (2,'Metal Hammer Promotion',0,300,UNIX_TIMESTAMP('2011-08-09 00:00:00'),UNIX_TIMESTAMP('2011-09-15 00:00:00'),1,4,0,4);

insert into tb_accountLogTypes (i,name) values (5, 'Promotion applied');