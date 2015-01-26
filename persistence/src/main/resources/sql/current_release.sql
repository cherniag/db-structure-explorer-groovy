-- SRV-485 - [SERVER] CR 315: Change Promotions plan for MTV1 community

SET AUTOCOMMIT = 0;
START TRANSACTION;

SELECT @userGroupId := c.id
FROM tb_communities c JOIN tb_userGroups ug ON ug.community = c.id
WHERE c.name = 'mtv1';

SET @startPromoEndDateTime := UNIX_TIMESTAMP('2015-03-02 23:59:59');

UPDATE
  tb_promotions
SET
  startDate = UNIX_TIMESTAMP('2014-09-23 14:32:29'),
  endDate   = @startPromoEndDateTime
WHERE
  label = 'mtv1.starting.promo.audio'
  AND userGroup = @userGroupId
  AND @startPromoEndDateTime > UNIX_TIMESTAMP();

SET @fourWeeksStartDateTime := UNIX_TIMESTAMP('2015-02-03 00:00:00');

UPDATE
  tb_promotions
SET
  startDate = @fourWeeksStartDateTime
WHERE
  label = 'mtv1.4weeks.promo.audio'
  AND userGroup = @userGroupId
  AND @fourWeeksStartDateTime > UNIX_TIMESTAMP();

UPDATE
  tb_promotions
SET
  endDate   = UNIX_TIMESTAMP('2020-12-01 02:00:00')
WHERE
  label = 'mtv1.4weeks.promo.audio'
  AND userGroup = @userGroupId;

UPDATE
    tb_users u JOIN tb_promoCode pC
      ON u.last_promo = pc.id
      JOIN tb_promotions p
      ON pc.promotionId = p.i
SET
  u.nextSubPayment         = p.endDate,
  u.freeTrialExpiredMillis = p.endDate * 1000
WHERE
  p.label = 'mtv1.starting.promo.audio'
  AND u.userGroup = @userGroupId
  AND u.freeTrialExpiredMillis = u.nextSubPayment * 1000
  AND @startPromoEndDateTime > UNIX_TIMESTAMP();

COMMIT;

SET AUTOCOMMIT = 1;