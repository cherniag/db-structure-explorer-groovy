-- ----------
-- CL-6308 Server > Hot track channels > Add channels to tracks
-- ----------
alter table tb_chartDetail add column channel varchar(255);
alter table tb_chartUpdateDetail add column channel varchar(255);
-- ----------
-- Added offers and offers items
-- ----------
create table offer_items (
	offer_id integer not null,
	item_id integer not null
);

create table offers (
	id integer not null auto_increment, 
	community_id tinyint, 
	currency varchar(255), 
	price decimal(19,2), 
	title varchar(255) not null, 
	coverFileName varchar(255) not null,
	description varchar(255) not null,
	primary key (id)
);

create table offers_filters (
	offers_id integer not null, 
	filterWithCtiteria_id integer not null, 
	primary key (offers_id, filterWithCtiteria_id)
);

alter table offer_items add index FK2A28301DCB572DB7 (offer_id), add constraint FK2A28301DCB572DB7 foreign key (offer_id) references offers (id);
alter table offers add index FKC337319782282017 (community_id), add constraint FKC337319782282017 foreign key (community_id) references tb_communities (i);
alter table offers_filters add index FK8373F5536D1B889C (offers_id), add constraint FK8373F5536D1B889C foreign key (offers_id) references offers (id);
alter table offers_filters add index FK8373F553FC727FFF (filterWithCtiteria_id), add constraint FK8373F553FC727FFF foreign key (filterWithCtiteria_id) references filters (id);

alter table tb_media add column type int(10);
-- ----------
-- CL-6445 Server > Refactor payments to support end to end flow for PP and PSMS > Implement temporary payment details
-- ----------
start transaction;
	alter table tb_paymentDetails add column owner_id int unsigned;
	alter table tb_paymentDetails add index owner_payment_details(owner_id);
	update tb_paymentDetails set owner_id=(select u.i from tb_users as u where u.currentPaymentDetailsId=tb_paymentDetails.i);
commit;

-- ----------
-- IMP-383 remove info column from Item entity and add one to ChartDetail  
-- ----------
alter table tb_chartDetail add column info TEXT;
alter table tb_chartUpdateDetail add column info TEXT;
update tb_chartDetail set info=(select tb_media.info from tb_media where tb_chartDetail.media = tb_media.i);

-- ----------
-- ----------
-- CL-6429 Server > Web store > Special offer flow > Implementation
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-6429
-- ---------- 
alter table tb_paymentPolicy add column availableInStore bit not null default false;

-- CL-6432: Server > Web store > One click subscription
-- http://jira.dev.now-technologies.mobi:8181/browse/CL-6432
alter table tb_submittedPayments add column paymentDetailsId BIGINT;
alter table tb_pendingPayments add column paymentDetailsId BIGINT;

alter table tb_submittedPayments add index FKF1EF29B7197A82E5 (paymentDetailsId), add constraint FKF1EF29B7197A82E5 foreign key (paymentDetailsId) references tb_paymentDetails (i);
alter table tb_pendingPayments add index FK999C3E13197A82E5 (paymentDetailsId), add constraint FK999C3E13197A82E5 foreign key (paymentDetailsId) references tb_paymentDetails (i);

insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "3.4.0", "Big Eagle");