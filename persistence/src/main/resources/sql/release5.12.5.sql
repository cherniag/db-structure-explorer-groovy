insert into system (release_time_millis, version, release_name) values(unix_timestamp(), "5.12.5", "5.12.5");

-- SRV-478
SET autocommit = 0;
START TRANSACTION;

/*update message*/

set @id = (select id from client_version_messages where message_key='service.config.mtv.tracks.CURRENT.IOS.0.1.0' and url is null);

UPDATE client_version_messages 
SET 
    message_key = 'service.config.mtv.tracks.FORCED_UPDATE.IOS.0.1.0',
    url = 'http://preview.mtvtrax.com/'
WHERE
    id = @id;

/*update client version*/

set @ios = (select i from tb_deviceTypes where name='IOS');
set @community_id = (select id from tb_communities where name='mtv1');
    

UPDATE client_version_info 
SET 
    status = 'FORCED_UPDATE'
WHERE
    message_id = @id
        AND device_type_id = @ios
        AND community_id = @community_id
        AND major_number = 0
        AND minor_number = 1
        AND revision_number = 0
        AND status = 'CURRENT'
        AND application_name = 'mtv-tracks';
    

COMMIT;
SET autocommit = 1;
-- End of SRV-478