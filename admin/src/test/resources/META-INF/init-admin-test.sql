-- insert into users (username,communityURL,password                          ,enabled) VALUES ('a'     ,'nowtop40'  ,'21b0304492b4e80831d66abd78514f29',true   );
-- insert into authorities (username, authority) values ('a','ROLE_USER');

create table users (
    username varchar(50) not null,
    communityURL varchar(255) not null,
    password varchar(50) not null,
    enabled boolean not null,
    primary key (username, communityURL)
);

create table authorities (
    username varchar(50) not null,
    authority varchar(50) not null,
    foreign key (username) references users (username),
    unique index authorities_idx_1 (username, authority)
);

-- Test data
insert into users (username, communityURL, password, enabled) values ('admin', 'nowtop40', 'ceb4f32325eda6142bd65215f4c0f371', true);
-- insert into users (username, communityURL, password, enabled) values ('admin', 'occ', 'ceb4f32325eda6142bd65215f4c0f371', true);
-- insert into users (username, communityURL, password, enabled) values ('admin', 'chartsnow', 'ceb4f32325eda6142bd65215f4c0f371', true);
-- insert into users (username, communityURL, password, enabled) values ('admin', 'samsung', 'ceb4f32325eda6142bd65215f4c0f371', true);
insert into authorities (username, authority) values ('admin','ROLE_ADMIN');