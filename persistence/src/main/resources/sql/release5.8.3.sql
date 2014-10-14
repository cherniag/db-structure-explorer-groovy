-- SRV-285 [SERVER] Problem with ITunes subscription for MTV
SET autocommit = 0;
START TRANSACTION;

select @mtv1_community_id:= c.id from tb_communities c where c.name = 'mtv1';

delete from tb_paymentPolicy
where communityID = @mtv1_community_id and paymentType='iTunesSubscription' and provider = 'GOOGLE_PLUS';

update tb_paymentPolicy
set provider = null
where communityID = @mtv1_community_id and paymentType='iTunesSubscription';

COMMIT;