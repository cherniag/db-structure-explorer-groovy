/* REMOVE INCONSISTENT DEVICE USER DATA */
  set autocommit = 0;
  start transaction;
    -- create table for storing removed records
    CREATE TABLE `backup_device_user_data` (
      `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
      `community_url` varchar(255) NOT NULL,
      `user_id` int(11) NOT NULL,
      `xtify_token` char(255) NOT NULL,
      `device_uid` char(255) NOT NULL,
      PRIMARY KEY (`id`),
      UNIQUE KEY `xtify_token` (`xtify_token`),
      KEY `user_id` (`user_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

    -- insert into remove table device_user_data ids which refer to the same user, all records besides the last one
    insert into backup_device_user_data(`id`,`community_url`,`user_id`,`xtify_token`,`device_uid`)
                                 select `id`,`community_url`, `user_id`, `xtify_token`,`device_uid`
    from device_user_data d where
    d.id <> (select max(id) from device_user_data d2 where d2.user_id = d.user_id and d2.device_uid = d.device_uid );

    -- insert into remove table device_user_data ids which refers to removed users
    insert into backup_device_user_data(`id`,`community_url`,`user_id`,`xtify_token`,`device_uid`)
                                 select `id`,`community_url`,`user_id`,`xtify_token`,`device_uid`
    from device_user_data d where
    d.user_id not in (select i from tb_users)
    and d.id not in (select id from backup_device_user_data);

    -- remove and commit
    delete from device_user_data where id in (select id from backup_device_user_data) ;
  commit;

/* END */

-- add constraint to avoid creation duplicates for the same user
alter table device_user_data add constraint `userID_deviceUID` UNIQUE(`user_id`, `device_uid`);