/* REMOVE INCONSISTENT DEVICE USER DATA */
  create table device_user_data_to_remove(
    id int not null primary key
  );

  start transaction;
    -- insert into remove table device_user_data ids which refer to the same user, all records besides the last one
    insert into device_user_data_to_remove
    select id from device_user_data d
    where
    d.id <> (select max(id) from device_user_data d2 where d2.user_id = d.user_id and d2.device_uid = d.device_uid );

    -- insert into remove table device_user_data ids which refers to removed users
    insert into device_user_data_to_remove
    select id from device_user_data d
    where
    d.user_id not in (select i from tb_users);

    -- run commit
    delete from device_user_data where id in (select id from device_user_data_to_remove) ;
  commit;

  drop table device_user_data_to_remove;
/* END */

alter table device_user_data add constraint `userID_deviceUID` UNIQUE(`user_id`, `device_uid`);