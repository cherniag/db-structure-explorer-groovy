 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "3.9-SN", "3.9-SN");

 -- IMP-1365 O2 Tracks - Pop Up needed for too many download attempts in 24 hours
alter table user_logs add column phoneNumber char(25);
alter table user_logs add column type char(25) default 'UPDATE_O2_USER';

 --IMP-1198 [jAdmin] Ability to schedule updates to the covers and sub-titles on the home screen
alter table tb_chartDetail add column image_filename varchar(255);
alter table tb_chartDetail add column image_title varchar(255);
alter table tb_chartDetail add column subtitle char(50);
alter table tb_chartDetail add column title char(50);
alter table tb_chartDetail modify column chgPosition int null;
alter table tb_chartDetail modify column prevPosition tinyint null;
alter table tb_chartDetail modify column media integer unsigned null;

alter table tb_charts drop column subtitle;
alter table tb_charts drop column image_filename;

insert into tb_charts (name, numTracks, genre, timestamp, numBonusTracks, type)
select
'Fourth Chart',
ch.numTracks,
ch.genre,
ch.timestamp,
ch.numBonusTracks,
'FOURTH_CHART'
from tb_charts ch
join community_charts cc on cc.chart_id = ch.i
join tb_communities c on cc.community_id = c.i and c.rewriteURLParameter = 'o2'
where ch.type='BASIC_CHART';

insert into community_charts (chart_id, community_id)
select
ch.i,
c.i
from tb_charts ch
join tb_communities c on c.rewriteURLParameter = 'o2'
where ch.type = 'FOURTH_CHART';

 -- for remove provious chart updates
delete from tb_chartDetail where media is null;
insert into tb_chartDetail (chart, position, publishTimeMillis, version, title)
select
ch.i,
1,
unix_timestamp('2013-01-01')*1000,
0,
ch.name
from tb_charts ch
join community_charts cc on cc.chart_id = ch.i
join tb_communities c on cc.community_id = c.i and c.rewriteURLParameter = 'o2'
where ch.type='BASIC_CHART';

insert into tb_chartDetail (chart, position, publishTimeMillis, version, title)
select
ch.i,
2,
unix_timestamp('2013-01-01')*1000,
0,
ch.name
from tb_charts ch
join community_charts cc on cc.chart_id = ch.i
join tb_communities c on cc.community_id = c.i and c.rewriteURLParameter = 'o2'
where ch.type='HOT_TRACKS';

insert into tb_chartDetail (chart, position, publishTimeMillis, version, title)
select
ch.i,
3,
unix_timestamp('2013-01-01')*1000,
0,
ch.name
from tb_charts ch
join community_charts cc on cc.chart_id = ch.i
join tb_communities c on cc.community_id = c.i and c.rewriteURLParameter = 'o2'
where ch.type='OTHER_CHART';

insert into tb_chartDetail (chart, position, publishTimeMillis, version, title)
select
ch.i,
0,
unix_timestamp('2013-01-01')*1000,
0,
ch.name
from tb_charts ch
join community_charts cc on cc.chart_id = ch.i
join tb_communities c on cc.community_id = c.i and c.rewriteURLParameter = 'o2'
where ch.type='FOURTH_CHART';

-- IMP-1263 [BILLING] Track users changing segment and unsubscribe them
alter table tb_paymentPolicy add column provider char(255);

update tb_paymentPolicy pp set pp.provider = 'non-o2' where pp.segment is null and pp.communityID = 10
update tb_paymentPolicy pp set pp.provider = 'o2' where pp.segment is not null and pp.communityID = 10

 -- IMP-1261 [MOBILE WEB] Error Messaging
alter table tb_paymentDetails add column errorCode varchar(255);

-- [Jadmin] Add rich popup which allows deeplinking to various "anchors" in the app/mobile web
-- http://jira.musicqubed.com/browse/IMP-1542          
alter table messages add column actionType varchar(255);
alter table messages add column action varchar(255);
alter table messages add column actionButtonText varchar(255);
