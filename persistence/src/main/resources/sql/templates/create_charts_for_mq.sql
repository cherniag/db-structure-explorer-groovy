INSERT INTO tb_charts (name, numTracks, genre, timestamp, numBonusTracks, type) VALUES ('HOT_TRACKS_1 FOR MQ', 0, 1, 1389367950, 0, 'HOT_TRACKS');
INSERT INTO tb_charts (name, numTracks, genre, timestamp, numBonusTracks, type) VALUES ('HOT_TRACKS_2 FOR MQ', 0, 1, 1389367950, 0, 'FIFTH_CHART');
INSERT INTO tb_charts (name, numTracks, genre, timestamp, numBonusTracks, type) VALUES ('HOT_TRACKS_3 FOR MQ', 0, 1, 1389367950, 0, 'MQ_PLAYLIST_1');
INSERT INTO tb_charts (name, numTracks, genre, timestamp, numBonusTracks, type) VALUES ('HOT_TRACKS_4 FOR MQ', 0, 1, 1389367950, 0, 'MQ_PLAYLIST_2');
INSERT INTO tb_charts (name, numTracks, genre, timestamp, numBonusTracks, type) VALUES ('HOT_TRACKS_5 FOR MQ', 0, 1, 1389367950, 0, 'OTHER_CHART');
INSERT INTO tb_charts (name, numTracks, genre, timestamp, numBonusTracks, type) VALUES ('HOT_TRACKS_6 FOR MQ', 0, 1, 1389367950, 0, 'FOURTH_CHART');

INSERT INTO community_charts (chart_id, community_id) VALUES ((select i from tb_charts where name='HOT_TRACKS_1 FOR MQ'), (select id from tb_communities where rewriteURLParameter='mq'));
INSERT INTO community_charts (chart_id, community_id) VALUES ((select i from tb_charts where name='HOT_TRACKS_2 FOR MQ'), (select id from tb_communities where rewriteURLParameter='mq'));
INSERT INTO community_charts (chart_id, community_id) VALUES ((select i from tb_charts where name='HOT_TRACKS_3 FOR MQ'), (select id from tb_communities where rewriteURLParameter='mq'));
INSERT INTO community_charts (chart_id, community_id) VALUES ((select i from tb_charts where name='HOT_TRACKS_4 FOR MQ'), (select id from tb_communities where rewriteURLParameter='mq'));
INSERT INTO community_charts (chart_id, community_id) VALUES ((select i from tb_charts where name='HOT_TRACKS_5 FOR MQ'), (select id from tb_communities where rewriteURLParameter='mq'));
INSERT INTO community_charts (chart_id, community_id) VALUES ((select i from tb_charts where name='HOT_TRACKS_6 FOR MQ'), (select id from tb_communities where rewriteURLParameter='mq'));