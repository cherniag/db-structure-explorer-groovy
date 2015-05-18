SET autocommit = 0;
start transaction;

select @first_new_id:=coalesce(max(id), 0) + 1 from client_version_messages;
set @second_new_id = @first_new_id + 1;
set @third_new_id = @first_new_id + 2;
set @forth_new_id = @first_new_id + 3;

insert into client_version_messages
(id            ,             message_key                                   , url) values
(@first_new_id , 'service.config.mtv.tracks.FORCED_UPDATE.WINDOWS.1.0.6-p' , 'http://www.windowsphone.com/s?appid=73e8aedd-babf-4e94-87d6-899464f1c49c'),
(@second_new_id, 'service.config.mtv.tracks.FORCED_UPDATE.WINDOWS.1.0.7-p' , 'http://www.windowsphone.com/s?appid=73e8aedd-babf-4e94-87d6-899464f1c49c'),
(@third_new_id , 'service.config.mtv.tracks.FORCED_UPDATE.WINDOWS.1.0.10-p', 'http://www.windowsphone.com/s?appid=73e8aedd-babf-4e94-87d6-899464f1c49c'),
(@forth_new_id , 'service.config.mtv.tracks.FORCED_UPDATE.ANDROID.2.0.0'   , 'https://play.google.com/store/apps/details?id=com.musicqubed.mtv');

select @community_id:=id from tb_communities where name='mtv1';
select @windows:=i from tb_deviceTypes where name='WINDOWS_PHONE';
select @android:=i from tb_deviceTypes where name='ANDROID';

insert into client_version_info
(device_type_id, community_id ,  major_number, minor_number, revision_number, qualifier, message_id    , status         , application_name) values
( @windows     , @community_id, 1            , 0           , 6              , 'p'      , @first_new_id , 'FORCED_UPDATE', 'mtv-tracks'),
( @windows     , @community_id, 1            , 0           , 7              , 'p'      , @second_new_id, 'FORCED_UPDATE', 'mtv-tracks'),
( @windows     , @community_id, 1            , 0           , 10             , 'p'      , @third_new_id , 'FORCED_UPDATE', 'mtv-tracks'),
( @android     , @community_id, 2            , 0           , 0              , null     , @forth_new_id , 'FORCED_UPDATE', 'mtv-tracks');

commit;
SET autocommit = 1;