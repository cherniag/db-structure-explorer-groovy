set autocommit = 0;
start transaction;

select @userGroupId:= ug.id from tb_communities c join tb_userGroups ug on ug.community = c.id where c.name = 'mtv1';

set @promoCodeName = 'mtv1.easter2015.4days';
set @promoStrartUnixTimeSeconds = UNIX_TIMESTAMP('2015-04-03 00:00:00');
set @promoExpirationUnixTimeSeconds = UNIX_TIMESTAMP('2015-04-06 23:59:00');

INSERT INTO tb_promotions
(description   , numUsers, maxUsers, startDate                  , endDate                        , isActive, freeWeeks, subWeeks, userGroup   , type       , showPromotion, label         , is_white_listed) VALUES
(@promoCodeName, 0       , 0       , @promoStrartUnixTimeSeconds, @promoExpirationUnixTimeSeconds, FALSE   , 0        , 0       , @userGroupId, 'PromoCode', FALSE        , @promoCodeName, FALSE);

INSERT INTO tb_promoCode
(code          , promotionId)  VALUES
(@promoCodeName, (SELECT p.i FROM tb_promotions p WHERE p.label = @promoCodeName));

select @lastPromoCodeId := id from tb_promoCode where code = @promoCodeName;
select @subscribedStatusId := i from tb_userStatus WHERE name = 'SUBSCRIBED';
select @limitedStatusId := i from tb_userStatus WHERE name = 'LIMITED';

set @promoStrartUnixTimeMillis = @promoStrartUnixTimeSeconds*1000;
set @promoExpirationUnixTimeMillis = @promoExpirationUnixTimeSeconds*1000;

update tb_users
  left join tb_paymentDetails on tb_users.currentPaymentDetailsId = tb_paymentDetails.i
set
  tb_users.nextSubPayment = @promoExpirationUnixTimeSeconds,
  tb_users.freeTrialStartedTimestampMillis = @promoStrartUnixTimeMillis,
  tb_users.freeTrialExpiredMillis = @promoExpirationUnixTimeMillis,
  tb_users.status = @subscribedStatusId,
  tb_users.last_promo = @lastPromoCodeId
where
  tb_users.userGroup = @userGroupId
  and tb_users.status = @limitedStatusId
  and (tb_paymentDetails.i is null or tb_paymentDetails.activated is false);

update tb_users
set
  tb_users.nextSubPayment = @promoExpirationUnixTimeSeconds,
  tb_users.freeTrialExpiredMillis = @promoExpirationUnixTimeMillis,
  tb_users.status = @subscribedStatusId,
  tb_users.last_promo = @lastPromoCodeId
where
  tb_users.userGroup = @userGroupId
  and tb_users.freeTrialExpiredMillis>UNIX_TIMESTAMP()*1000
  and tb_users.freeTrialExpiredMillis < @promoExpirationUnixTimeMillis;

select @promoByPromoCodeAccountLogType := i from tb_accountLogTypes where name = 'Promotion by promo code applied';

INSERT INTO tb_accountLog
(userUID  , transactionType                , balanceAfter, logTimestamp               , promoCode) SELECT
tb_users.i, @promoByPromoCodeAccountLogType, 0           , @promoStrartUnixTimeSeconds, @promoCodeName
from tb_users where tb_users.last_promo = @lastPromoCodeId and tb_users.freeTrialStartedTimestampMillis = @promoStrartUnixTimeMillis;

INSERT INTO tb_accountLog
(userUID  , transactionType                , balanceAfter, logTimestamp                        , promoCode) SELECT
tb_users.i, @promoByPromoCodeAccountLogType, 0           , tb_users.freeTrialExpiredMillis/1000, @promoCodeName
from tb_users where tb_users.last_promo = @lastPromoCodeId and tb_users.freeTrialStartedTimestampMillis != @promoStrartUnixTimeMillis;

-- save result as csv file
select i, userName from tb_users where last_promo = @lastPromoCodeId;

commit;
set autocommit = 1;
