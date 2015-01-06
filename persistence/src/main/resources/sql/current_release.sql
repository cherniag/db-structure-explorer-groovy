-- SRV-490 - [SERVER] CR 376: MTVNZ Promotion setup for initial usage

SET AUTOCOMMIT = 0;
START TRANSACTION;

SELECT @userGroupId := c.id
FROM tb_communities c JOIN tb_userGroups ug ON ug.community = c.id
WHERE c.name = 'mtv1';

SET @startPromoEndDateTime := UNIX_TIMESTAMP('2015-02-23 23:59:59');

UPDATE
  tb_promotions
SET
  startDate = UNIX_TIMESTAMP('2014-09-23 14:32:29'),
  endDate   = @startPromoEndDateTime
WHERE
  label = 'mtv1.starting.promo.audio'
  AND userGroup = @userGroupId
  AND @startPromoEndDateTime > UNIX_TIMESTAMP();

UPDATE
  tb_promotions
SET
  startDate = UNIX_TIMESTAMP('2015-01-27 00:00:00'),
  endDate   = UNIX_TIMESTAMP('2020-12-01 02:00:00')
WHERE
  label = 'mtv1.4weeks.promo.audio'
  AND userGroup = @userGroupId;

UPDATE
    tb_users u JOIN tb_promotions p
      ON u.last_promo = p.i
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