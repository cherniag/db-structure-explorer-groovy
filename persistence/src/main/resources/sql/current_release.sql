-- http://jira.musicqubed.com/browse/SRV-263
-- [JADMIN] Allow content manager to define the method of how to play tracks and playlists in Magazine Channel

alter table sz_deeplink_music_track add column player_type VARCHAR(255) not null DEFAULT 'REGULAR_PLAYER_ONLY';
alter table sz_deeplink_music_list add column player_type VARCHAR(255) not null DEFAULT 'REGULAR_PLAYER_ONLY';