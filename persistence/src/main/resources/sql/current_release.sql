-- http://jira.musicqubed.com/browse/SRV-289
-- [SERVER] Activate 2 weeks promotion in MTV Prod community

set AUTOCOMMIT=0;
START TRANSACTION;

select @userGroupId:= c.id from tb_communities c join tb_userGroups ug on ug.community = c.id where c.name = 'mtv1';

UPDATE
    tb_promotions p JOIN tb_userGroups ug
      ON p.userGroup = ug.id JOIN tb_communities c
      ON c.id = ug.community
         AND c.name = 'mtv1'
         AND p.label = 'mtv1.promo.2weeks.audio'
         AND p.freeWeeks != 0
SET
  p.isActive = true
;

commit;