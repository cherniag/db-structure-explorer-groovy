-- http://jira.dev.now-technologies.mobi:8181/browse/CL-5567
-- Important! Last nmber is an ID of the samsung community
-- VERY Important!!!! Add fake user to tb_users with the same community and code='06c3cf627e57f573c8694e0119cf1c50'
-- 1.
insert into tb_apps (model, jad, appType, communityID) values
('SamsungNoApp', '{"result":{"successful":"false", "url":"http://m.chartsnow.mobi/TestAppNotReady"}}', 5, 7),
('Android', '{"result":{"successful":"false", "url":"http://m.chartsnow.mobi/No_app"}}', 5, 7),
('Samsung', '{"result":{"successful":"true", "url":"market://details?id=mobi.chartsnow.test"}}', 5, 7);
-- 2.
insert into tb_users (displayName, title, firstName, lastName, userName, subBalance, freeBalance, token, status, deviceType, device, userGroup, userType, lastDeviceLogin, lastWebLogin, nextSubPayment, lastPaymentTx, Address1, Address2, City, Postcode, Country, mobile, code, sessionID, ipAddress, tempToken, deviceString, canContact, paymentType, operator, pin, paymentEnabled, paymentStatus, numPsmsRetries, age, gender, facebookId, lastSuccessfulPaymentTimeMillis, currentPaymentDetailsId, potentialPromotion_i)
values   ("samsungFakeOTAUser", "", "", "", "samsungotauser@cn.com", 0, 0, MD5(CONCAT('8z54YKmns9Qz','123456','8z54YKmns9Qz','user1@cn.com','8z54YKmns9Qz')), 10, 2, "", 7, 0, 0, 0, unix_timestamp('2012-04-12 00:00:00'), 0, "", "", "", "", 1, "", "06c3cf627e57f573c8694e0119cf1c50", "", "", "", "", 1, "UNKNOWN", 1, "", 1, 1, 0, null, null, "", 0, null, null);

-- http://jira.dev.now-technologies.mobi:8181/browse/CL-5569
-- App > Auto register > Server
alter table tb_users add column deviceUID varchar(255);
create unique index `deviceUID` on `tb_users`(`deviceUID`,`userGroup`);

create table tb_promotedDevice(
    deviceUID VARCHAR(255) not null,
    primary key (deviceUID)
);
create unique index `DEVICE_UID_INDEX` on tb_promotedDevice(deviceUID);
alter table tb_users add column deviceModel varchar(255);

-- CL-6114: Production bug: Couldn't save entity for table tb_useriPhoneDetails and tb_userAndroidDetails
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-6114
-- drop table hibernate_sequences;  We can't drop it because the portal project uses persistence project of old version
alter table tb_userAndroidDetails modify column i int(11) NOT NULL auto_increment;

alter table tb_useriPhoneDetails modify column userUID int(10) unsigned NOT NULL;

alter table tb_userAndroidDetails modify column userUID int(10) unsigned NOT NULL;

alter table tb_userAndroidDetails add index INDEX_ON_ANDROID_USERUID_TB_USER_I(userUID),
add constraint INDEX_ON_ANDROID_USERUID_TB_USER_I foreign key
(
   userUID
)
references tb_users(i);

alter table tb_userAndroidDetails add index INDEX_ON_IPHONE_USERUID_TB_USER_I(userUID),
add constraint INDEX_ON_IPHONE_USERUID_TB_USER_I foreign key
(
   userUID
)
references tb_users(i);