 -- Final insert of the release version
insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "3.9-SN", "3.9-SN");

 -- IMP-1365 O2 Tracks - Pop Up needed for too many download attempts in 24 hours
alter table user_logs add column phoneNumber char(25);
alter table user_logs add column type char(25) default 'UPDATE_O2_USER';

 -- IMP-1198 [jAdmin] Ability to schedule updates to the covers and sub-titles on the home screen
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
insert into tb_chartDetail (subtitle, image_filename, chart, position, publishTimeMillis, version, title)
select
'The Official Top 40',
'CHART_1369842177974_10',
ch.i,
1,
unix_timestamp('2013-01-01')*1000,
0,
ch.name
from tb_charts ch
join community_charts cc on cc.chart_id = ch.i
join tb_communities c on cc.community_id = c.i and c.rewriteURLParameter = 'o2'
where ch.type='BASIC_CHART';

insert into tb_chartDetail (subtitle, image_filename, chart, position, publishTimeMillis, version, title)
select
'The Hottest New Releases',
'CHART_1369842274293_20',
ch.i,
2,
unix_timestamp('2013-01-01')*1000,
0,
ch.name
from tb_charts ch
join community_charts cc on cc.chart_id = ch.i
join tb_communities c on cc.community_id = c.i and c.rewriteURLParameter = 'o2'
where ch.type='HOT_TRACKS';

insert into tb_chartDetail (subtitle, image_filename, chart, position, publishTimeMillis, version, title)
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

insert into tb_chartDetail (subtitle, chart, position, publishTimeMillis, version, title)
select
'Album Choice',
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

 -- IMP-1498 [EPIC] Allow user to select a playlist tailored to their taste
 alter table tb_chartDetail add column locked BIT default false
 alter table tb_chartDetail add column defaultChart BIT default false
 
 create table user_charts (user_id integer not null, chart_id tinyint not null)
 alter table user_charts add constraint FK_chart_id foreign key (chart_id) references tb_charts
 alter table user_charts add constraint FK_user_id foreign key (user_id) references tb_users
 
insert into tb_charts (name, numTracks, genre, timestamp, numBonusTracks, type)
select
'Other Chart Not Default',
ch.numTracks,
ch.genre,
ch.timestamp,
ch.numBonusTracks,
'OTHER_CHART'
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
where ch.type = 'OTHER_CHART' and ch.name='Other Chart Not Default';

update tb_chartDetail cd
join tb_charts ch on ch.i = cd.chart and ch.type='OTHER_CHART'
join community_charts cc on cc.chart_id = ch.i
join tb_communities c on cc.community_id = c.i and c.rewriteURLParameter = 'o2'
set cd.defaultChart = true;

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
where ch.type='OTHER_CHART' and ch.name='Other Chart Not Default';

-- Implement a badge on IOS Home Screen when new App content available
-- http://jira.musicqubed.com/browse/IMP-1385
alter table tb_useriPhoneDetails add column last_push_of_content_update_millis bigint(20) default 0 NOT NULL;
alter table tb_userAndroidDetails add column last_push_of_content_update_millis bigint(20) default 0  NOT NULL;

update tb_useriPhoneDetails set tb_useriPhoneDetails.last_push_of_content_update_millis=UNIX_TIMESTAMP()*1000;
update tb_userAndroidDetails set tb_userAndroidDetails.last_push_of_content_update_millis=UNIX_TIMESTAMP()*1000;

-- [Jadmin] Add rich popup which allows deeplinking to various "anchors" in the app/mobile web
-- http://jira.musicqubed.com/browse/IMP-1542          
alter table messages add column actionType varchar(255);
alter table messages add column action varchar(255);
alter table messages add column actionButtonText varchar(255);

-- [Server] Create the 5th playlist - VIP
-- http://jira.musicqubed.com/browse/IMP-1548
INSERT INTO tb_charts( genre , name , subtitle , numBonusTracks , numTracks , TIMESTAMP , type ) VALUES( 1 , 'VIP Playlist' , '', 0 , 10 , UNIX_TIMESTAMP() , 'FIFTH_CHART' ) ;
select @chart_id:=tb_charts.i from tb_charts where tb_charts.type='FIFTH_CHART';
INSERT INTO community_charts (chart_id, community_id) select @chart_id, tb_communities.i from tb_communities where tb_communities.name='o2';
INSERT INTO tb_chartDetail( chart , POSITION , media , prevPosition , chgPosition , channel , info , publishTimeMillis , VERSION , image_filename , image_title , subtitle , title , locked , defaultChart ) VALUES( (select @chart_id) , 5 , NULL , NULL , NULL , NULL , NULL , UNIX_TIMESTAMP()*1000 , 0 , NULL , NULL , 'Especially For You' , 'VIP Playlist' , FALSE , TRUE ) ;

--IMP-1631 [jAdmin] Change the way of showing playlist names\
update tb_chartDetail cd 
join tb_charts ch on ch.i = cd.chart and ch.type='BASIC_CHART'
set cd.title = 'Official Top 40' where cd.media is null

update tb_chartDetail cd 
join tb_charts ch on ch.i = cd.chart and ch.type='HOT_TRACKS'
set cd.title = 'Just In' where cd.media is null

update tb_chartDetail cd 
join tb_charts ch on ch.i = cd.chart and ch.type='OTHER_CHART'
set cd.title = 'Your playlist' where cd.media is null

update tb_chartDetail cd 
join tb_charts ch on ch.i = cd.chart and ch.type='FOURTH_CHART'
set cd.title = 'Mainstage' where cd.media is null