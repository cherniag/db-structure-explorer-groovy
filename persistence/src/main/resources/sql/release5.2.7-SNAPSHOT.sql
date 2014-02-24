start transaction;

update tb_paymentPolicy
set is_default = false
where communityID=(select ug.id from tb_userGroups ug, tb_communities tc
where ug.community = tc.id and tc.rewriteURLParameter='o2') and is_default = true  and media_type='AUDIO' and subWeeks = 1 and tariff='_3G';


update tb_paymentPolicy
set is_default = true
where communityID=(select ug.id from tb_userGroups ug, tb_communities tc
where ug.community = tc.id and tc.rewriteURLParameter='o2') and is_default = false  and media_type='AUDIO' and subWeeks = 2  and tariff='_3G';

commit;
