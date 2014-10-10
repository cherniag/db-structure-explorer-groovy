insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "5.8.2", "5.8.2");

-- http://jira.musicqubed.com/browse/SRV-260
-- [SERVER] MTV1 community set up additional 2 playlists

SET autocommit = 0;
START TRANSACTION;

select @mtv1_community_id:= c.id from tb_communities c where c.name = 'mtv1';

INSERT INTO tb_charts (name, numTracks, genre, timestamp            , numBonusTracks, type) VALUES
('HL_UK_PLAYLIST_4 - MTV1' , 0        , 1    , UNIX_TIMESTAMP(now()), 0             , 'HL_UK_PLAYLIST_2'),
('HL_UK_PLAYLIST_5 - MTV1' , 0        , 1    , UNIX_TIMESTAMP(now()), 0             , 'HL_UK_PLAYLIST_2');

INSERT INTO community_charts (chart_id                             , community_id) VALUES
((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_4 - MTV1'), @mtv1_community_id),
((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_5 - MTV1'), @mtv1_community_id);

INSERT INTO tb_chartDetail (chart                                , position, media, prevPosition, chgPosition, channel, info, publishTimeMillis         , version, image_filename, image_title              , subtitle                 , title                    , locked, defaultChart) VALUES
((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_4 - MTV1'), 6       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP(now())*1000, 1      , NULL          , 'HL_UK_PLAYLIST_4 - MTV1', 'HL_UK_PLAYLIST_4 - MTV1', 'HL_UK_PLAYLIST_4 - MTV1', NULL  , NULL);

INSERT INTO tb_chartDetail (chart                                , position, media, prevPosition, chgPosition, channel, info, publishTimeMillis         , version, image_filename, image_title              , subtitle                 , title                    , locked, defaultChart) VALUES
((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_5 - MTV1'), 7       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP(now())*1000, 1      , NULL          , 'HL_UK_PLAYLIST_5 - MTV1', 'HL_UK_PLAYLIST_5 - MTV1', 'HL_UK_PLAYLIST_5 - MTV1', NULL  , NULL);

commit;


-- SRV-269
SET autocommit = 0;
START TRANSACTION;

select @community_id:= c.id from tb_communities c where c.name = 'demo4';

INSERT INTO tb_charts (name, numTracks, genre, timestamp            , numBonusTracks, type) VALUES
  ('HL_UK_PLAYLIST_6 FOR D4' , 0        , 1    , UNIX_TIMESTAMP(now()), 0             , 'HL_UK_PLAYLIST_2'),
  ('HL_UK_PLAYLIST_7 FOR D4' , 0        , 1    , UNIX_TIMESTAMP(now()), 0             , 'HL_UK_PLAYLIST_2');

INSERT INTO community_charts (chart_id                             , community_id) VALUES
  ((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_6 FOR D4'), @community_id),
  ((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_7 FOR D4'), @community_id);

INSERT INTO tb_chartDetail (chart                                , position, media, prevPosition, chgPosition, channel, info, publishTimeMillis         , version, image_filename, image_title              , subtitle                 , title                    , locked, defaultChart) VALUES
  ((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_6 FOR D4'), 6       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP(now())*1000, 1      , NULL          , 'HL_UK_PLAYLIST_6 FOR D4', 'HL_UK_PLAYLIST_6 FOR D4', 'HL_UK_PLAYLIST_6 FOR D4', NULL  , NULL);

INSERT INTO tb_chartDetail (chart                                , position, media, prevPosition, chgPosition, channel, info, publishTimeMillis         , version, image_filename, image_title              , subtitle                 , title                    , locked, defaultChart) VALUES
  ((SELECT i FROM tb_charts WHERE name = 'HL_UK_PLAYLIST_7 FOR D4'), 7       , NULL , NULL        , NULL       , NULL   , NULL, UNIX_TIMESTAMP(now())*1000, 1      , NULL          , 'HL_UK_PLAYLIST_7 FOR D4', 'HL_UK_PLAYLIST_7 FOR D4', 'HL_UK_PLAYLIST_7 FOR D4', NULL  , NULL);

commit;
-- SRV-269


