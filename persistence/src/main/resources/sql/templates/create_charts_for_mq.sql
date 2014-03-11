INSERT INTO tb_charts (name, numTracks, genre, timestamp, numBonusTracks, type) VALUES ('HOT_TRACKS FOR MQ', 0, 1, UNIX_TIMESTAMP(now()), 0, 'HOT_TRACKS');
INSERT INTO tb_charts (name, numTracks, genre, timestamp, numBonusTracks, type) VALUES ('FIFTH_CHART FOR MQ', 0, 1, UNIX_TIMESTAMP(now()), 0, 'FIFTH_CHART');
INSERT INTO tb_charts (name, numTracks, genre, timestamp, numBonusTracks, type) VALUES ('MQ_PLAYLIST_1 FOR MQ', 0, 1, UNIX_TIMESTAMP(now()), 0, 'MQ_PLAYLIST_1');
INSERT INTO tb_charts (name, numTracks, genre, timestamp, numBonusTracks, type) VALUES ('MQ_PLAYLIST_2 FOR MQ', 0, 1, UNIX_TIMESTAMP(now()), 0, 'MQ_PLAYLIST_2');
INSERT INTO tb_charts (name, numTracks, genre, timestamp, numBonusTracks, type) VALUES ('OTHER_CHART FOR MQ', 0, 1, UNIX_TIMESTAMP(now()), 0, 'OTHER_CHART');
INSERT INTO tb_charts (name, numTracks, genre, timestamp, numBonusTracks, type) VALUES ('FOURTH_CHART FOR MQ', 0, 1, UNIX_TIMESTAMP(now()), 0, 'FOURTH_CHART');

INSERT INTO community_charts (chart_id, community_id) VALUES ((select i from tb_charts where name='HOT_TRACKS FOR MQ'), (select id from tb_communities where rewriteURLParameter='mq'));
INSERT INTO community_charts (chart_id, community_id) VALUES ((select i from tb_charts where name='FIFTH_CHART FOR MQ'), (select id from tb_communities where rewriteURLParameter='mq'));
INSERT INTO community_charts (chart_id, community_id) VALUES ((select i from tb_charts where name='MQ_PLAYLIST_1 FOR MQ'), (select id from tb_communities where rewriteURLParameter='mq'));
INSERT INTO community_charts (chart_id, community_id) VALUES ((select i from tb_charts where name='MQ_PLAYLIST_2 FOR MQ'), (select id from tb_communities where rewriteURLParameter='mq'));
INSERT INTO community_charts (chart_id, community_id) VALUES ((select i from tb_charts where name='OTHER_CHART FOR MQ'), (select id from tb_communities where rewriteURLParameter='mq'));
INSERT INTO community_charts (chart_id, community_id) VALUES ((select i from tb_charts where name='FOURTH_CHART FOR MQ'), (select id from tb_communities where rewriteURLParameter='mq'));