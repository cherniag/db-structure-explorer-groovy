 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "4.0-SN", "4.0-SN");

 -- IMP-1776 [Server] Update the Get Chart command to include Video
alter table tb_files add column duration int unsigned not null;

insert into tb_charts (name, numTracks, genre, timestamp, numBonusTracks, type)
select
'Video Chart',
ch.numTracks,
ch.genre,
ch.timestamp,
ch.numBonusTracks,
'VIDEO_CHART'
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
where ch.type = 'VIDEO_CHART';

insert into tb_chartDetail (subtitle, chart, position, publishTimeMillis, version, title)
select
'Video Chart',
ch.i,
0,
unix_timestamp('2013-01-01')*1000,
0,
ch.name
from tb_charts ch
join community_charts cc on cc.chart_id = ch.i
join tb_communities c on cc.community_id = c.i and c.rewriteURLParameter = 'o2'
where ch.type='VIDEO_CHART';

