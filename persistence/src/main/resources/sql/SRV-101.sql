ALTER TABLE sz_deeplink_promotional ADD COLUMN opener varchar(15);

update sz_deeplink_promotional
set opener = 'IN_APP' where link_type = 'EXTERNAL_AD';
