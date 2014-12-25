insert into system (release_time_millis, version, release_name) values(unix_timestamp(), "5.12.4", "5.12.4");

-- SRV-469
SET autocommit = 0;
START TRANSACTION;
/*setup messages*/
set @last_id_1 = (select count(id) from client_version_messages) + 1;
insert into client_version_messages(id, message_key, url) values(@last_id_1, 'service.config.mtv.tracks.FORCED_UPDATE.IOS.0.1.0', 'http://preview.mtvtrax.com/');

set @last_id_2 = (select count(id) from client_version_messages) + 1;
insert into client_version_messages(id, message_key, url) values(@last_id_2, 'service.config.mtv.tracks.FORCED_UPDATE.IOS.1.0.0', 'https://itunes.apple.com/gb/app/mtv-trax-free-music-player/id925265172');

set @last_id_3 = (select count(id) from client_version_messages) + 1 ;
insert into client_version_messages(id, message_key, url) values(@last_id_3, 'service.config.mtv.tracks.FORCED_UPDATE.IOS.1.0.2', 'https://itunes.apple.com/gb/app/mtv-trax-free-music-player/id925265172');


/*setup client versions*/
set @ios = (select i from tb_deviceTypes where name='IOS');
set @community_id = (select id from tb_communities where name='mtv1');

insert into client_version_info (device_type_id, community_id, major_number, minor_number, revision_number, qualifier, message_id, status,         application_name)
                         values ( @ios,         @community_id, 0,            1,            0,               null,     @last_id_1, 'FORCED_UPDATE', 'mtv-tracks'),
                                ( @ios,         @community_id, 1,            0,            0,               null,     @last_id_2, 'FORCED_UPDATE', 'mtv-tracks'),
                                ( @ios,         @community_id, 1,            0,            2,               null,     @last_id_3, 'FORCED_UPDATE', 'mtv-tracks');


COMMIT;
SET autocommit = 1;
-- End of SRV-469