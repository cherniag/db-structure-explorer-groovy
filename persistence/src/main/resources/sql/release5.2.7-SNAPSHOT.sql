update tb_promotions ps, tb_promoCode pc
set ps.isActive = 0
where pc.promotionId = ps.i and pc.code='promo8' and ps.userGroup = 10  and ps.freeWeeks = 8  and ps.isActive = 1;

INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed)
VALUES (53, 'o2 4 Free Weeks', 0, 0, 1356342064, 1606780800, 1, 4, 0, 10, 'PromoCode', 0, null, false);

update tb_promotions ps, tb_promoCode pc
set pc.promotionId = 53
where pc.promotionId = ps.i and pc.code='promo8' and ps.userGroup = 10  and ps.freeWeeks = 8  and ps.isActive = 0;


update tb_promotions ps, tb_promoCode pc
set ps.isActive = 0
where pc.promotionId = ps.i and pc.code='o2.consumer.4g.paym.direct' and ps.userGroup = 10  and ps.freeWeeks = 8  and ps.isActive = 1;

INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed)
VALUES (54, 'o2 4 weeks Video Audio Free Trial for 4G PAYM direct consumers after 2013', 0, 0, 1388527200, 2147483647, 1, 4, 0, 10, 'PromoCode', 0, 'o2.consumer.4g.paym.direct.after.end.of.2013', false);

update tb_promotions ps, tb_promoCode pc
set pc.promotionId = 54
where pc.promotionId = ps.i and pc.code='o2.consumer.4g.paym.direct' and ps.userGroup = 10  and ps.freeWeeks = 8  and ps.isActive = 0;



update tb_promotions ps, tb_promoCode pc
set ps.isActive = 0
where pc.promotionId = ps.i and pc.code='o2.consumer.4g.paym.indirect' and ps.userGroup = 10  and ps.freeWeeks = 8  and ps.isActive = 1;

INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed)
VALUES (55, 'o2 4 weeks Video Audio Free Trial for 4G PAYM indirect consumers', 0, 0, 1377220905, 2147483647, 1, 4, 0, 10, 'PromoCode', 0, 'o2.consumer.4g.paym.indirect', false);

update tb_promotions ps, tb_promoCode pc
set pc.promotionId = 55
where pc.promotionId = ps.i and pc.code='o2.consumer.4g.paym.indirect' and ps.userGroup = 10  and ps.freeWeeks = 8  and ps.isActive = 0;


update tb_promotions ps, tb_promoCode pc
set ps.isActive = 0
where pc.promotionId = ps.i and pc.code='o2.consumer.4g.payg' and ps.userGroup = 10  and ps.freeWeeks = 8  and ps.isActive = 1;

INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed)
 VALUES (56, 'o2 4 weeks Video Audio Free Trial for 4G PAYG consumers', 0, 0, 1377220915, 2147483647, 1, 4, 0, 10, 'PromoCode', 0, 'o2.consumer.4g.payg', false);

update tb_promotions ps, tb_promoCode pc
set pc.promotionId = 56
where pc.promotionId = ps.i and pc.code='o2.consumer.4g.payg' and ps.userGroup = 10  and ps.freeWeeks = 8  and ps.isActive = 0;


update tb_paymentPolicy
set is_default = false
where communityID=10 and is_default = true  and media_type='AUDIO' and subWeeks = 1 and tariff='_3G';


update tb_paymentPolicy
set is_default = true
where communityID=10 and is_default = false  and media_type='AUDIO' and subWeeks = 2  and tariff='_3G';



