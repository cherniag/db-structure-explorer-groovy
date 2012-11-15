-- CL-2335: User can pay by one policy and get weeks by other policy rules
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-2335
alter table tb_payments add column subweeks TINYINT UNSIGNED not null;
update tb_payments set subweeks =
(
   select
   subWeeks
   from tb_paymentPolicy
   where tb_paymentPolicy.operator =
   (
      select
      operator
      from tb_users
      where i=tb_payments.userUID
   )
   and tb_paymentPolicy.paymentType =
   (
      select
      paymenttype
      from tb_users
      where i=tb_payments.userUID
   )
   and tb_paymentPolicy.communityID=
   (
      select
      community
      from tb_userGroups
      where tb_userGroups.i=
      (
         select
         userGroup
         from tb_users
         where i=tb_payments.userUID
      )
   )
)
where
(
   select
   subWeeks
   from tb_paymentPolicy
   where tb_paymentPolicy.operator =
   (
      select
      operator
      from tb_users
      where i=tb_payments.userUID
   )
   and tb_paymentPolicy.paymentType =
   (
      select
      paymenttype
      from tb_users
      where i=tb_payments.userUID
   )
   and tb_paymentPolicy.communityID=
   (
      select
      community
      from tb_userGroups
      where tb_userGroups.i= ( select userGroup from tb_users where i=tb_payments.userUID)
   )
)  is not null and status='PENDING'
;

alter table tb_accountLog engine=INNODB;
alter table tb_accountLogTypes engine=INNODB;
alter table tb_adminPermissions engine=INNODB;
alter table tb_adminPermissionsLabel engine=INNODB;
alter table tb_adminUserType engine=INNODB;
alter table tb_adminUsers engine=INNODB;
alter table tb_admin_pages engine=INNODB;
alter table tb_appTypes engine=INNODB;
alter table tb_appVersionCountry engine=INNODB;
alter table tb_appVersions engine=INNODB;
alter table tb_apps engine=INNODB;
alter table tb_artist engine=INNODB;
alter table tb_chartDetail engine=INNODB;
alter table tb_chartUpdateDetail engine=INNODB;
alter table tb_chartUpdates engine=INNODB;
alter table tb_charts engine=INNODB;
alter table tb_communities engine=INNODB;
alter table tb_country engine=INNODB;
alter table tb_deviceTypes engine=INNODB;
alter table tb_drm engine=INNODB;
alter table tb_drmPolicy engine=INNODB;
alter table tb_drmTypes engine=INNODB;
alter table tb_fileTypes engine=INNODB;
alter table tb_files engine=INNODB;
alter table tb_genres engine=INNODB;
alter table tb_labels engine=INNODB;
alter table tb_media engine=INNODB;
alter table tb_mediaLog engine=INNODB;
alter table tb_mediaLogTypes engine=INNODB;
alter table tb_menuItems engine=INNODB;
alter table tb_menus engine=INNODB;
alter table tb_news engine=INNODB;
alter table tb_newsDetail engine=INNODB;
alter table tb_newsUpdateDetail engine=INNODB;
alter table tb_newsUpdates engine=INNODB;
alter table tb_operators engine=INNODB;
alter table tb_page_menu_items engine=INNODB;
alter table tb_page_menus engine=INNODB;
alter table tb_pages engine=INNODB;
alter table tb_paymentPolicy engine=INNODB;
alter table tb_paymentStatus engine=INNODB;
alter table tb_paymentTypes engine=INNODB;
alter table tb_payments engine=INNODB;
alter table tb_promotions engine=INNODB;
alter table tb_support engine=INNODB;
alter table tb_systemLog engine=INNODB;
alter table tb_userGroups engine=INNODB;
alter table tb_userStatus engine=INNODB;
alter table tb_userTypes engine=INNODB;
alter table tb_users engine=INNODB;

-- CL-2942: Free weeks - server
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-2942

alter table tb_promotions add column type char (20) not null;

update tb_promotions set type ='noPromoCode';
create table tb_promoCode (id INT UNSIGNED not null auto_increment, code varchar (255) not null, promotionId TINYINT UNSIGNED not null, primary key (id));

insert into tb_paymentStatus (id, name) values (6, 'AWAITING_PAYMENT');

-- CL-3032: Separate free weeks and promotion in accountLog
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-3032
alter table tb_accountLogTypes modify name char(40);
insert into tb_accountLogTypes (i,name) values(6,'Promotion by promo code applied');

-- CL-3003: PayPal - Server
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-3003

insert into tb_paymentStatus (name) values ('AWAITING_PAY_PAL');
insert into tb_paymentStatus (name) values ('PAY_PAL_ERROR');

alter table tb_payments add column numPaymentRetries INT UNSIGNED, add column currencyCode VARCHAR(5);

-- Merge portal login page header for Now Community
update tb_communities SET displayName = "NOW! Official Top 40 Chart App" WHERE i=5

-- CL-3371: Wer registration - Server
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-3371
create index pmtUserStatus on tb_payments (status, userUID);

delete from tb_appVersionCountry where id=2;
delete from tb_country where i=2;

alter table tb_promotions add column showPromotion tinyint(1) not null;

create index pmtTxCode on tb_payments (internaltxcode);

-- CL-3474: Correct typo in the operators list
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-3474
update tb_operators set name='Truphone' where name='truephone';

-- CL-3475: Change mobile platforms list
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-3475
delete from tb_deviceTypes where name='PCTEST';
insert into tb_deviceTypes (i, name) values(6, 'SYMBIAN');

update tb_communities SET displayName = 'NOW! Top 40' WHERE i=5;
DELETE FROM `tb_userStatus` WHERE `tb_userStatus`.`i` =12 ;

insert into tb_country (i, name, fullName) values(2, 'UA', 'Ukraine');
insert into tb_appVersionCountry (id, appVersion_id, country_id) values(2, 1, 2);

-- Christophe Lemoine: This was applied on production long time ago for financial reporting:
alter table tb_accountLog add column (topupBalance int(10), amountUnused int(10), relatedTopup int(10));
