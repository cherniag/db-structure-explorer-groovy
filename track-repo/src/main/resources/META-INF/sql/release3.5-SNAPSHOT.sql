-- Adding status track to define state of track
alter table Track add column status VARCHAR(255) not null DEFAULT 'NONE';
-- Adding resolution track to define current resoltution 
alter table Track add column resolution VARCHAR(255) not null DEFAULT 'RATE_ORIGINAL';
-- Adding itunes URL field to reduce amount of calculations 
alter table Track add column itunesUrl VARCHAR(255) null;