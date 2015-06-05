create table users_to_remove(
    id int not null,
    success tinyint(1) DEFAULT 0,
    key(id)
);

insert into users_to_remove (id)
  select i from tb_users
  where userGroup < 10;