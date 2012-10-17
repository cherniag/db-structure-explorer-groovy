create table users (
    username varchar(50) not null,
    communityURL varchar(255) not null,
    password varchar(50) not null,
    enabled boolean not null,
    unique key username_community (username, communityURL)
) engine = InnoDb default charset=utf8;

create table authorities (
    username varchar(50) not null,
    authority varchar(50) not null,
    foreign key (username) references users (username),
    unique index authorities_idx_1 (username, authority)
) engine = InnoDb default charset=utf8;

-- Test data
insert into users (username, communityURL, password, enabled) values ("admin", "occ", md5(concat("admin", "{", "admin", "}")), true);
insert into users (username, communityURL, password, enabled) values ("admin", "chartsnow", md5(concat("admin", "{", "admin", "}")), true);
insert into users (username, communityURL, password, enabled) values ("admin", "samsung", md5(concat("admin", "{", "admin", "}")), true);
insert into authorities (username, authority) values ("admin","ROLE_ADMIN");

insert into users (username, communityURL, password, enabled) values ("mary@marygorman.com", "chartsnow", md5(concat("Cha3t5N0w", "{", "mary@marygorman.com", "}")), true);
insert into users (username, communityURL, password, enabled) values ("mary@marygorman.com", "samsung", md5(concat("Cha3t5N0w", "{", "mary@marygorman.com", "}")), true);
insert into authorities (username, authority) values ("mary@marygorman.com","ROLE_ADMIN");

insert into users (username, communityURL, password, enabled) values ("blair.gorman@musicqubed.com", "chartsnow", md5(concat("Cha3t5N0w1", "{", "blair.gorman@musicqubed.com", "}")), true);
insert into users (username, communityURL, password, enabled) values ("blair.gorman@musicqubed.com", "samsung", md5(concat("Cha3t5N0w1", "{", "blair.gorman@musicqubed.com", "}")), true);
insert into authorities (username, authority) values ("blair.gorman@musicqubed.com","ROLE_ADMIN");

insert into users (username, communityURL, password, enabled) values ("yana.z@musicqubed.com", "chartsnow", md5(concat("Cha3t5N0w1", "{", "yana.z@musicqubed.com", "}")), true);
insert into users (username, communityURL, password, enabled) values ("yana.z@musicqubed.com", "samsung", md5(concat("Cha3t5N0w1", "{", "yana.z@musicqubed.com", "}")), true);
insert into authorities (username, authority) values ("yana.z@musicqubed.com","ROLE_ADMIN");