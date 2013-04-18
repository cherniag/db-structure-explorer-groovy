create temporary table phoneNumbers(phoneNumber varchar(255));

insert into phoneNumbers (phoneNumber) SELECT u.userName FROM tb_users u where u.userName in ('+44##########', '+44##########' , '+44##########'); --some list numbers which should be applied promotions 

update tb_users u 
join tb_promotions p on p.label = 'staff' and p.userGroup = 10 
join phoneNumbers pn on pn.phoneNumber = u.userName 
set u.potentialPromoCodePromotion_i = null, 
u.nextSubPayment = p.endDate,
u.freeTrialExpiredMillis = (
CASE 
WHEN u.freeTrialExpiredMillis = u.nextSubPayment*1000 THEN p.endDate*1000
ELSE u.freeTrialExpiredMillis
END
), 
u.status=10, 
u.freeTrialStartedTimestampMillis=unix_timestamp()*1000;

update tb_promotions p
join (select count(pn.phoneNumber) as c from phoneNumbers pn) pc
set p.numUsers = p.numUsers + pc.c
where p.label = 'staff' and p.userGroup = 10;

insert into tb_accountLog (userUID, balanceAfter, transactionType, promoCode, logTimestamp) 
SELECT 
u.i,
p.freeWeeks,
6,
'staff',
unix_timestamp()
FROM tb_users u 
join tb_promotions p on p.label = 'staff' and p.userGroup = 10 
join phoneNumbers pn on pn.phoneNumber = u.userName;

drop table phoneNumbers;