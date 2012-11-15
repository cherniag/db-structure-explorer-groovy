-- CL-1769: Update DB with new community fileds and store all instances in static manner for applications usage
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-1769
alter table tb_communities add displayName varchar(100) not null, add assetName varchar(100) not null, add rewriteURLParameter varchar(50) not null;
update tb_communities set displayName='RBT Development',assetName='RBTDevelopment',rewriteURLParameter='RBTDevelopment' where i=1;
update tb_communities set displayName='CN QA Testing',assetName='CNQATesting',rewriteURLParameter='CNQATesting' where i=2;
update tb_communities set displayName='Charts Now',assetName='Charts Now',rewriteURLParameter='ChartsNow' where i=3;
update tb_communities set displayName='Metal Hammer',assetName='Metal Hammer',rewriteURLParameter='MetalHammer' where i=4;

alter table tb_media add column audioPreviewFile INT(10) UNSIGNED;
alter table tb_media add column headerPreviewFile INT(10) UNSIGNED;
alter table tb_media add column iTunesUrl varchar(255);

alter table tb_users add column paymentType varchar(50) not null;
update tb_users set paymentType = 'creditCard';
alter table tb_users add column operator int(10) unsigned not null;
alter table tb_users add column pin varchar(10) not null;
alter table tb_users add column paymentEnabled tinyint(1) not null;
update tb_users set paymentEnabled = true;

alter table tb_paymentPolicy add column paymentType varchar(50) not null;
alter table tb_paymentPolicy add operator int(10) unsigned not null;
update tb_paymentPolicy set paymentType = 'creditCard';

CREATE TABLE IF NOT EXISTS `tb_operators` (
  `i` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(100) NOT NULL,
  `migName` varchar(50) NOT NULL,
  PRIMARY KEY  (`i`)
) DEFAULT CHARSET=latin1 AUTO_INCREMENT=6;

INSERT INTO `tb_operators` (`i`, `name`, `migName`) VALUES
( 1, 'Orange UK', 'MIG01OU'),
( 2, 'Vodafone UK', 'MIG00VU'),
( 3, 'O2 UK', 'MIG01XU'),
( 4, 'T-Mobile UK', 'MIG01TU'),
( 5, 'Three UK', 'MIG01HU'),
( 6, 'ASDA Mobile', 'Not Specified'),
( 7, 'BT', 'Not Specified'),
( 8, 'Giffgaff', 'Not Specified'),
( 9, 'IDT Mobile', 'Not Specified'),
( 10, 'Talkmobile', 'Not Specified'),
( 11, 'TalkTalk', 'Not Specified'),
( 12, 'Tesco Mobile', 'Not Specified'),
( 13, 'truephone', 'Not Specified'),
( 14, 'Virgin Mobile', 'Not Specified');

-- CL-2018: Add operator, mobile and paymentStatus to AccountCheck and send to client
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-2018
create table if not exists tb_paymentStatus(id int unsigned NOT NULL auto_increment,
  name varchar(25) NOT NULL,
  PRIMARY KEY  (id)
);

insert into tb_paymentStatus (id,name) values(1,'NULL');
insert into tb_paymentStatus (id,name) values(2,'OK');
insert into tb_paymentStatus (id,name) values(3,'AWAITING_PSMS');
insert into tb_paymentStatus (id,name) values(4,'PSMS_ERROR');
insert into tb_paymentStatus (id,name) values(5,'PIN_PENDING');

alter table tb_users add column paymentStatus int unsigned NOT NULL;
update tb_users set paymentStatus=1;
alter table tb_users add column numPsmsRetries int unsigned NOT NULL;

-- CL-2108: Create Now SQL script and put assets to portal
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-2108
INSERT INTO tb_userGroups (i, name, community, chart, news, drmPolicy) VALUES ('5', 'Default Group', '5', '5', '5', '3');
insert into tb_news (i,name,numEntries,community,timestamp) values (5,'Now Music', 10, 5, UNIX_TIMESTAMP());

INSERT INTO tb_newsDetail( news, position ) VALUES ( 5, 1 ) ;
INSERT INTO tb_newsDetail( news, position ) VALUES ( 5, 2 ) ;
INSERT INTO tb_newsDetail( news, position ) VALUES ( 5, 3 ) ;
INSERT INTO tb_newsDetail( news, position ) VALUES ( 5, 4 ) ;
INSERT INTO tb_newsDetail( news, position ) VALUES ( 5, 5 ) ;
INSERT INTO tb_newsDetail( news, position ) VALUES ( 5, 6 ) ;
INSERT INTO tb_newsDetail( news, position ) VALUES ( 5, 7 ) ;
INSERT INTO tb_newsDetail( news, position ) VALUES ( 5, 8 ) ;
INSERT INTO tb_newsDetail( news, position ) VALUES ( 5, 9 ) ;
INSERT INTO tb_newsDetail( news, position ) VALUES ( 5, 10 ) ;

insert into tb_charts (i,name,numTracks,community,genre,timestamp) values (5,'Default Chart', 40, 5, 1, UNIX_TIMESTAMP());

insert into tb_chartDetail (chart, position, media) values (5,1,48);
insert into tb_chartDetail (chart, position, media) values (5,2,47);
insert into tb_chartDetail (chart, position, media) values (5,3,50);
insert into tb_chartDetail (chart, position, media) values (5,4,51);
insert into tb_chartDetail (chart, position, media) values (5,5,53);
insert into tb_chartDetail (chart, position, media) values (5,6,54);
insert into tb_chartDetail (chart, position, media) values (5,7,55);
insert into tb_chartDetail (chart, position, media) values (5,8,56);
insert into tb_chartDetail (chart, position, media) values (5,9,57);
insert into tb_chartDetail (chart, position, media) values (5,10,58);
insert into tb_chartDetail (chart, position, media) values (5,11,59);
insert into tb_chartDetail (chart, position, media) values (5,12,60);
insert into tb_chartDetail (chart, position, media) values (5,13,61);
insert into tb_chartDetail (chart, position, media) values (5,14,62);
insert into tb_chartDetail (chart, position, media) values (5,15,63);
insert into tb_chartDetail (chart, position, media) values (5,16,64);
insert into tb_chartDetail (chart, position, media) values (5,17,65);
insert into tb_chartDetail (chart, position, media) values (5,18,66);
insert into tb_chartDetail (chart, position, media) values (5,19,67);
insert into tb_chartDetail (chart, position, media) values (5,20,68);
insert into tb_chartDetail (chart, position, media) values (5,21,69);
insert into tb_chartDetail (chart, position, media) values (5,22,70);
insert into tb_chartDetail (chart, position, media) values (5,23,74);
insert into tb_chartDetail (chart, position, media) values (5,24,75);
insert into tb_chartDetail (chart, position, media) values (5,25,76);
insert into tb_chartDetail (chart, position, media) values (5,26,77);
insert into tb_chartDetail (chart, position, media) values (5,27,79);
insert into tb_chartDetail (chart, position, media) values (5,28,110);
insert into tb_chartDetail (chart, position, media) values (5,29,109);
insert into tb_chartDetail (chart, position, media) values (5,30,108);
insert into tb_chartDetail (chart, position, media) values (5,31,107);
insert into tb_chartDetail (chart, position, media) values (5,32,105);
insert into tb_chartDetail (chart, position, media) values (5,33,104);
insert into tb_chartDetail (chart, position, media) values (5,34,403);
insert into tb_chartDetail (chart, position, media) values (5,35,102);
insert into tb_chartDetail (chart, position, media) values (5,36,101);
insert into tb_chartDetail (chart, position, media) values (5,37,100);
insert into tb_chartDetail (chart, position, media) values (5,38,96);
insert into tb_chartDetail (chart, position, media) values (5,39,99);
insert into tb_chartDetail (chart, position, media) values (5,40,97);

INSERT INTO tb_apps (model, jad, jar, appType, communityID) VALUES
('a8181'	  ,'nowmusic_CN.apk'	,''		   ,1,5),
('Nexus S'	  ,'nowmusic_CN.apk' ,''		   ,1,5),
('Wildfire S'	  ,'nowmusic_CN.apk'	,''		   ,1,5),
('S510e'	  ,'nowmusic_CN.apk'	,''		   ,1,5),
('Desire HD'	  ,'nowmusic_CN.apk'	,''		   ,1,5),
('GT-I9100'	  ,'nowmusic_CN.apk'	,''		   ,1,5),
('BlackBerry 8520','CN_nowmusic.jad'	,'CN_nowmusic.cod',2,5),
('BlackBerry 9300','CN_nowmusic.jad'	,'CN_nowmusic.cod',2,5),
('BlackBerry 9700','CN_nowmusic.jad'	,'CN_nowmusic.cod',2,5),
('BlackBerry 9780','CN_nowmusic.jad'	,'CN_nowmusic.cod',2,5);

insert tb_communities (i,name, appVersion, communityTypeID, displayName, assetName, rewriteURLParameter) values(5, 'Now Music', 1, 5,'Now! That Is What I Call Music','Now Music','nowtop40');

-- CL-2150: Create short code for operator in payment policy
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-2150
alter table tb_paymentPolicy add column shortCode varchar(20) not null;

INSERT INTO tb_paymentPolicy (communityID, subWeeks, subCost, paymentType, operator, shortCode) VALUES ('5', '5', '5', 'creditCard', '0', '');
INSERT INTO tb_paymentPolicy (communityID, subWeeks, subCost, paymentType, operator, shortCode) VALUES ('5', '3', '4', 'PSMS', '2', '66968');
INSERT INTO tb_paymentPolicy (communityID, subWeeks, subCost, paymentType, operator, shortCode) VALUES ('5', '3', '4', 'PSMS', '4', '66968');
INSERT INTO tb_paymentPolicy (communityID, subWeeks, subCost, paymentType, operator, shortCode) VALUES ('5', '3', '4', 'PSMS', '14', '66968');
INSERT INTO tb_paymentPolicy (communityID, subWeeks, subCost, paymentType, operator, shortCode) VALUES ('5', '3', '4', 'PSMS', '1', '66968');
INSERT INTO tb_paymentPolicy (communityID, subWeeks, subCost, paymentType, operator, shortCode) VALUES ('5', '3', '4', 'PSMS', '5', '66968');

alter table tb_payments add column paymentType varchar(20) not null;
update tb_payments set tb_payments.paymentType=(select paymentType from tb_users where tb_users.i=tb_payments.userUID) where (select paymentType from tb_users where tb_users.i=tb_payments.userUID) is not null;

insert into tb_promotions (description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup) values ('Now Music Promotion', 0, 300, 1312844400, 1316127600, 1, 0, 3, 5);

update tb_apps set jad='CN_NowMusic.jad' where appType=2 and communityID=5 ;
update tb_apps set jar='CN_NowMusic.cod' where appType=2 and communityID=5 ;

create index pmt_user on tb_payments (useruuid);

-- alter table tb_accountLog add column (topupBalance int(10), amountUnused int(10), relatedTopup int(10)); For financial reporting!!!!!!!!!!!!!!!!!!!!!!!!
-- !!!!! experimantal start

-- CL-1919: Create server command GET_PAYMENT_POLICY
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-1919
-- insert into tb_paymentPolicy(i,communityID,subWeeks,subCost,paymentType,operator,shortCode) VALUES(6,4,4,4.62,'PSMS',1,'80988');

-- CL-1796: Create db scripts for model changes
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-1796
-- create table tb_paymentMethod (id INT UNSIGNED not null auto_increment, shortName varchar(50) not null, fullName varchar(150) not null,primary key (id));
-- insert into tb_paymentMethod (shortName,fullName) values ('CC','Credit Card');
-- insert into tb_paymentMethod (shortName,fullName) values ('PSMS','Premium Short Message Service');

-- CL-1838: Modify server commands for IPHONE clients
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-1838
-- insert into tb_userstatus (i,name) values(13, 'SUBSCRIBED_IOS');

-- CL-1951: Describe SEND_PIN command, add entry to tb_userStatus
-- delete from tb_userStatus where i=7;
-- insert into tb_userStatus (i,name) values(7,'PIN_PENDING');

-- !!!!! experimantal end