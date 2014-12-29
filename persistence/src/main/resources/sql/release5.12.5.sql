insert into system (release_time_millis, version, release_name) values(unix_timestamp(), "5.12.5", "5.12.5");

-- SRV-478
SET autocommit = 0;
START TRANSACTION;
/*update message*/
set @id = (select id from client_version_messages where message_key='service.config.mtv.tracks.CURRENT.IOS.0.1.0' and url is null);
update client_version_messages
set
  message_key = 'service.config.mtv.tracks.FORCED_UPDATE.IOS.0.1.0',
  url = 'http://preview.mtvtrax.com/'
where id = @id;

/*update client version*/
set @ios = (select i from tb_deviceTypes where name='IOS');
set @community_id = (select id from tb_communities where name='mtv1');

update client_version_info
set
    status = 'FORCED_UPDATE'
where
    message_id = @id and device_type_id = @ios and community_id = @community_id and major_number = 0 and minor_number = 1 and revision_number = 0 and status = 'CURRENT' and application_name = 'mtv-tracks';

COMMIT;
SET autocommit = 1;
-- End of SRV-478