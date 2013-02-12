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
 
alter table tb_users add base64_encoded_app_store_receipt varchar(255), add app_store_original_transaction_id varchar(255);
 
alter table tb_paymentpolicy add app_store_product_id varchar(255);

alter table tb_submittedPayments add next_sub_payment int add base64_encoded_app_store_receipt varchar(255), add app_store_original_transaction_id varchar(255);