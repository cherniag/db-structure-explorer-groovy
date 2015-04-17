--SRV-739 [SERVER] Force upgrade mtv1 old iOS clients.
SET autocommit = 0;
start transaction;

select @first_new_id:=coalesce(max(id), 0) + 1 from client_version_messages;
set @second_new_id = @first_new_id + 1;
set @third_new_id = @first_new_id + 2;

insert into client_version_messages
(id            ,             message_key                             , url) values
(@first_new_id , 'service.config.mtv.tracks.FORCED_UPDATE.IOS.2.0.0' , 'http://preview.mtvtrax.com/'),
(@second_new_id, 'service.config.mtv.tracks.FORCED_UPDATE.IOS.2.1.0' , 'https://itunes.apple.com/gb/app/mtv-trax-free-music-player/id925265172'),
(@third_new_id , 'service.config.mtv.tracks.FORCED_UPDATE.IOS.2.2.0' , 'https://itunes.apple.com/gb/app/mtv-trax-free-music-player/id925265172');

select @community_id:=id from tb_communities where name='mtv1';
select @ios:=i from tb_deviceTypes where name='IOS';

insert into client_version_info
(device_type_id, community_id ,  major_number, minor_number, revision_number, qualifier, message_id    , status         , application_name) values
( @ios         , @community_id, 2            , 0           , 0              , null     , @first_new_id , 'FORCED_UPDATE', 'mtv-tracks'),
( @ios         , @community_id, 2            , 1           , 0              , null     , @second_new_id, 'FORCED_UPDATE', 'mtv-tracks'),
( @ios         , @community_id, 2            , 2           , 0              , null     , @third_new_id , 'FORCED_UPDATE', 'mtv-tracks');

commit;
SET autocommit = 1;