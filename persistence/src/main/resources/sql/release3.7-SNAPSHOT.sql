 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "3.7.2-SN", "3.7.2-SN");

alter table tb_charts add column type varchar(255);

 -- start migration old chart structure to new three parts structure
 
update tb_charts
set type='BASIC_CHART', name='Basic Chart';
 
insert into tb_charts (name, numTracks, community, genre, timestamp, numBonusTracks, type)
select
'Hot Tracks',
ch.numTracks,
ch.community,
ch.genre,
ch.timestamp,
ch.numBonusTracks,
'HOT_TRACKS'
from tb_charts ch
where ch.type='BASIC_CHART';

insert into tb_charts (name, numTracks, community, genre, timestamp, numBonusTracks, type)
select
'Other Chart',
ch.numTracks,
ch.community,
ch.genre,
ch.timestamp,
ch.numBonusTracks,
'OTHER_CHART'
from tb_charts ch
where ch.type='BASIC_CHART';

update tb_chartDetail cd
join tb_charts cho on cho.i = cd.chart
join tb_charts chn on cho.community = chn.community and chn.type='HOT_TRACKS'
set cd.chart = chn.i
where cd.position <= 50 and cd.channel is not null;

update tb_chartDetail cd
join tb_charts cho on cho.i = cd.chart
join tb_charts chn on cho.community = chn.community and chn.type='OTHER_CHART'
set cd.chart = chn.i
where cd.position > 50 and cd.channel is not null;

 -- end migration

alter table tb_charts add column subtitle char(50) not null default '';

alter table tb_charts add column image_filename varchar(255);

create table community_charts (chart_id tinyint not null, community_id tinyint not null);

alter table community_charts add constraint FK3410E96B82282017 foreign key (community_id) references tb_communities;
alter table community_charts add constraint FK3410E96B4E1D2677 foreign key (chart_id) references tb_charts;

 -- start migration old one-to-many association between char and comunities to new many-to-many
 
update tb_charts
set subtitle=name;
 
insert into community_charts (chart_id, community_id)
select
ch.i,
ch.community
from tb_charts ch

 -- remove community column from tb_charts
SELECT @SQL:=concat('alter table tb_charts drop foreign key ',constraint_name) FROM information_schema.key_column_usage where constraint_schema = 'cn_service' and table_name = 'tb_charts' and column_name = 'community'; 
PREPARE stmt FROM @SQL; 
EXECUTE stmt;

alter table tb_charts drop column community;

 -- end migration

-- IMP-743 iOS Implement in-app purchase and subscription
alter table tb_users add base64_encoded_app_store_receipt longtext, add app_store_original_transaction_id varchar(255), add last_subscribed_payment_system varchar(255);
 
alter table tb_paymentPolicy add app_store_product_id varchar(255);

alter table tb_submittedPayments add next_sub_payment int, add base64_encoded_app_store_receipt longtext, add app_store_original_transaction_id varchar(255);

-- migration on monthly payment system for non o2 user of com

update tb_users u
set u.nextSubPayment = u.nextSubPayment + u.subBalance * 7 * 86400, u.subBalance = 0
where u.currentPaymentDetailsId IS NOT NULL and u.provider IS NOT NULL and u.provider <> 'o2';

-- end migration
