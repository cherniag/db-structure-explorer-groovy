-- BEGIN SRV-209
alter table `sz_update` drop index `sz_update`;

alter table `sz_update` add constraint `sz_update_updated_community` UNIQUE(`community_id`, `updated`);

-- END SRV-209