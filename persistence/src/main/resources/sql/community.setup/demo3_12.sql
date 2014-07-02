set autocommit=0;
start transaction;

use cn_service;

delete from community_charts where chart_id in(
  select i from tb_charts where name in (
    'BASIC_CHART - DEMO3',
    'HOT_TRACKS - DEMO3',
    'FIFTH_CHART - DEMO3',
    'HL_UK_PLAYLIST_1 - DEMO3',
    'HL_UK_PLAYLIST_2 - DEMO3',
    'OTHER_CHART - DEMO3')
);

delete from tb_chartDetail where chart in (
  select i from tb_charts where name in (
    'BASIC_CHART - DEMO3',
    'HOT_TRACKS - DEMO3',
    'FIFTH_CHART - DEMO3',
    'HL_UK_PLAYLIST_1 - DEMO3',
    'HL_UK_PLAYLIST_2 - DEMO3',
    'OTHER_CHART - DEMO3'
  )
);

update tb_userGroups set chart=null where community = (select id from tb_communities where name = 'demo3');
delete from tb_charts where name in (
  'BASIC_CHART - DEMO3',
  'HOT_TRACKS - DEMO3',
  'FIFTH_CHART - DEMO3',
  'HL_UK_PLAYLIST_1 - DEMO3',
  'HL_UK_PLAYLIST_2 - DEMO3',
  'OTHER_CHART - DEMO3'
);

INSERT INTO tb_charts (name, numTracks, genre, timestamp, numBonusTracks, type) VALUES
  ('OTHER_CHART_1 - DEMO3', 0, 1, UNIX_TIMESTAMP(now()), 0, 'OTHER_CHART'),
  ('OTHER_CHART_7 - DEMO3', 0, 1, UNIX_TIMESTAMP(now()), 0, 'OTHER_CHART'),
  ('BASIC_CHART_2 - DEMO3', 0, 1, UNIX_TIMESTAMP(now()), 0, 'BASIC_CHART'),
  ('BASIC_CHART_3 - DEMO3', 0, 1, UNIX_TIMESTAMP(now()), 0, 'BASIC_CHART'),
  ('BASIC_CHART_4 - DEMO3', 0, 1, UNIX_TIMESTAMP(now()), 0, 'BASIC_CHART'),
  ('BASIC_CHART_5 - DEMO3', 0, 1, UNIX_TIMESTAMP(now()), 0, 'BASIC_CHART'),
  ('BASIC_CHART_6 - DEMO3', 0, 1, UNIX_TIMESTAMP(now()), 0, 'BASIC_CHART'),
  ('BASIC_CHART_8 - DEMO3', 0, 1, UNIX_TIMESTAMP(now()), 0, 'BASIC_CHART'),
  ('BASIC_CHART_9 - DEMO3', 0, 1, UNIX_TIMESTAMP(now()), 0, 'BASIC_CHART'),
  ('BASIC_CHART_10 - DEMO3', 0, 1, UNIX_TIMESTAMP(now()), 0, 'BASIC_CHART'),
  ('BASIC_CHART_11 - DEMO3', 0, 1, UNIX_TIMESTAMP(now()), 0, 'BASIC_CHART'),
  ('BASIC_CHART_12 - DEMO3', 0, 1, UNIX_TIMESTAMP(now()), 0, 'BASIC_CHART');
update tb_userGroups set chart=(select i from tb_charts where name='OTHER_CHART_1 - DEMO3') where community = (select id from tb_communities where name = 'demo3');

INSERT INTO community_charts (chart_id, community_id) VALUES
  ((select i from tb_charts where name = 'OTHER_CHART_1 - DEMO3'), (select id from tb_communities where name = 'demo3')),
  ((select i from tb_charts where name = 'OTHER_CHART_7 - DEMO3'), (select id from tb_communities where name = 'demo3')),
  ((select i from tb_charts where name = 'BASIC_CHART_2 - DEMO3'), (select id from tb_communities where name = 'demo3')),
  ((select i from tb_charts where name = 'BASIC_CHART_3 - DEMO3'), (select id from tb_communities where name = 'demo3')),
  ((select i from tb_charts where name = 'BASIC_CHART_4 - DEMO3'), (select id from tb_communities where name = 'demo3')),
  ((select i from tb_charts where name = 'BASIC_CHART_5 - DEMO3'), (select id from tb_communities where name = 'demo3')),
  ((select i from tb_charts where name = 'BASIC_CHART_6 - DEMO3'), (select id from tb_communities where name = 'demo3')),
  ((select i from tb_charts where name = 'BASIC_CHART_8 - DEMO3'), (select id from tb_communities where name = 'demo3')),
  ((select i from tb_charts where name = 'BASIC_CHART_9 - DEMO3'), (select id from tb_communities where name = 'demo3')),
  ((select i from tb_charts where name = 'BASIC_CHART_10 - DEMO3'), (select id from tb_communities where name = 'demo3')),
  ((select i from tb_charts where name = 'BASIC_CHART_11 - DEMO3'), (select id from tb_communities where name = 'demo3')),
  ((select i from tb_charts where name = 'BASIC_CHART_12 - DEMO3'), (select id from tb_communities where name = 'demo3'));

insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'OTHER_CHART_1 - DEMO3'),0,null,null,null,null,null,UNIX_TIMESTAMP(now()) * 1000,1,null,'OTHER_CHART_1 - DEMO3','Chart','Playlist 1',null,null);
insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'OTHER_CHART_7 - DEMO3'),0,null,null,null,null,null,UNIX_TIMESTAMP(now()) * 1000,1,null,'OTHER_CHART_7 - DEMO3','Chart','Playlist 7',null,null);

insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'BASIC_CHART_2 - DEMO3'),0,null,null,null,null,null,UNIX_TIMESTAMP(now()) * 1000,1,null,'BASIC_CHART_2 - DEMO3','Basic','Playlist 2',null,null);
insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'BASIC_CHART_3 - DEMO3'),0,null,null,null,null,null,UNIX_TIMESTAMP(now()) * 1000,1,null,'BASIC_CHART_3 - DEMO3','Basic','Playlist 3',null,null);
insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'BASIC_CHART_4 - DEMO3'),0,null,null,null,null,null,UNIX_TIMESTAMP(now()) * 1000,1,null,'BASIC_CHART_4 - DEMO3','Basic','Playlist 4',null,null);
insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'BASIC_CHART_5 - DEMO3'),0,null,null,null,null,null,UNIX_TIMESTAMP(now()) * 1000,1,null,'BASIC_CHART_5 - DEMO3','Basic','Playlist 5',null,null);
insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'BASIC_CHART_6 - DEMO3'),0,null,null,null,null,null,UNIX_TIMESTAMP(now()) * 1000,1,null,'BASIC_CHART_6 - DEMO3','Basic','Playlist 6',null,null);
insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'BASIC_CHART_8 - DEMO3'),0,null,null,null,null,null,UNIX_TIMESTAMP(now()) * 1000,1,null,'BASIC_CHART_8 - DEMO3','Basic','Playlist 8',null,null);
insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'BASIC_CHART_9 - DEMO3'),0,null,null,null,null,null,UNIX_TIMESTAMP(now()) * 1000,1,null,'BASIC_CHART_9 - DEMO3','Basic','Playlist 9',null,null);
insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'BASIC_CHART_10 - DEMO3'),0,null,null,null,null,null,UNIX_TIMESTAMP(now()) * 1000,1,null,'BASIC_CHART_10 - DEMO3','Basic','Playlist 10',null,null);
insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'BASIC_CHART_11 - DEMO3'),0,null,null,null,null,null,UNIX_TIMESTAMP(now()) * 1000,1,null,'BASIC_CHART_11 - DEMO3','Basic','Playlist 11',null,null);
insert into tb_chartDetail (chart, position, media, prevPosition, chgPosition, channel, info, publishTimeMillis, version, image_filename, image_title, subtitle, title, locked, defaultChart) values
  ((select i from tb_charts where name = 'BASIC_CHART_12 - DEMO3'),0,null,null,null,null,null,UNIX_TIMESTAMP(now()) * 1000,1,null,'BASIC_CHART_12 - DEMO3','Basic','Playlist 12',null,null);

commit;
