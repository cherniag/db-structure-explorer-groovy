-- begin SRV-291
SET autocommit = 0;
START TRANSACTION;

select @mtv1_community_id:= c.id from tb_communities c where c.name = 'mtv1';

update tb_paymentPolicy
set subCost = 4.99, subWeeks = 4
where communityID = @mtv1_community_id and paymentType in ('iTunesSubscription', 'PAY_PAL');

COMMIT;
-- end SRV-291