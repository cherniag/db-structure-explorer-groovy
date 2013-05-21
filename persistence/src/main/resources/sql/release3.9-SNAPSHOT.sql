 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "3.9-SN", "3.9-SN");

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
where ch.type='BASIC_CHART';

insert into community_charts (chart_id, community_id)
select
ch.i,
cc.community_id
from tb_charts ch
join tb_charts ch1 on ch1.timestamp = ch.timestamp and ch1.type = 'BASIC_CHART'
join community_charts cc on cc.chart_id = ch1.i
where ch.type = 'FOURTH_CHART';

 -- for remove provious chart updates
delete from tb_chartDetails where title is not null;

 -- IMP-1365 O2 Tracks - Pop Up needed for too many download attempts in 24 hours
alter table user_logs add column phoneNumber char(25);
alter table user_logs add column type char(25) default 'UPDATE_O2_USER';

