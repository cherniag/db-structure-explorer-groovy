start transaction;

update tb_promotions ps
set ps.isActive = 0
where ps.i = 23;
--HOW TO CALCULATE THIS ID:
--select ps.i from tb_promotions ps, tb_promoCode pc where pc.promotionId = ps.i and pc.code='promo8' and ps.userGroup = 10  and ps.freeWeeks = 8  and ps.isActive = 1
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed)
VALUES (53, 'o2 4 Free Weeks', 0, 0, 1356342064, 1606780800, 1, 4, 0, (select ug.id from tb_userGroups ug, tb_communities tc
where ug.community = tc.id and tc.rewriteURLParameter='o2'), 'PromoCode', 0, 'o2 4 Free Weeks', false);
INSERT INTO tb_promoCode (code, promotionId, media_type)
VALUES ('promo4', 53, 'AUDIO');



update tb_promotions ps
set ps.isActive = 0
where ps.i = 23;
--HOW TO CALCULATE THIS ID:
--select ps.i from tb_promotions ps, tb_promoCode pc where pc.promotionId = ps.i and pc.code='o2.consumer.4g.paym.direct' and ps.userGroup = 10  and ps.freeWeeks = 8  and ps.isActive = 1
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed)
VALUES (54, 'o2 4 weeks Video Audio Free Trial for 4G PAYM direct consumers after 2013', 0, 0, 1388527200, 2147483647, 1, 4, 0, (select ug.id from tb_userGroups ug, tb_communities tc
where ug.community = tc.id and tc.rewriteURLParameter='o2'), 'PromoCode', 0, 'o2.consumer.4g.paym.direct.after.end.of.2013.4w', false);
INSERT INTO tb_promoCode (code, promotionId, media_type)
VALUES ('o2.consumer.4g.paym.direct.4weeks', 54, 'VIDEO_AND_AUDIO');


update tb_promotions ps
set ps.isActive = 0
where ps.i = 23;
--HOW TO CALCULATE THIS ID:
--select ps.i from tb_promotions ps, tb_promoCode pc where pc.promotionId = ps.i and pc.code='o2.consumer.4g.paym.indirect' and ps.userGroup = 10  and ps.freeWeeks = 8  and ps.isActive = 1
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed)
VALUES (55, 'o2 4 weeks Video Audio Free Trial for 4G PAYM indirect consumers', 0, 0, 1377220905, 2147483647, 1, 4, 0, (select ug.id from tb_userGroups ug, tb_communities tc
where ug.community = tc.id and tc.rewriteURLParameter='o2'), 'PromoCode', 0, 'o2.consumer.4g.paym.indirect.4w', false);
INSERT INTO tb_promoCode (code, promotionId, media_type)
VALUES ('o2.consumer.4g.paym.indirect.4weeks', 55, 'VIDEO_AND_AUDIO');



update tb_promotions ps
set ps.isActive = 0
where ps.i = 23;
--HOW TO CALCULATE THIS ID:
--select ps.i from tb_promotions ps, tb_promoCode pc where pc.promotionId = ps.i and pc.code='o2.consumer.4g.payg' and ps.userGroup = 10  and ps.freeWeeks = 8  and ps.isActive = 1
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed)
 VALUES (56, 'o2 4 weeks Video Audio Free Trial for 4G PAYG consumers', 0, 0, 1377220915, 2147483647, 1, 4, 0, (select ug.id from tb_userGroups ug, tb_communities tc
where ug.community = tc.id and tc.rewriteURLParameter='o2'), 'PromoCode', 0, 'o2.consumer.4g.payg.4w', false);
INSERT INTO tb_promoCode (code, promotionId, media_type)
VALUES ('o2.consumer.4g.payg.4weeks', 56, 'VIDEO_AND_AUDIO');


update tb_paymentPolicy
set is_default = false
where communityID=(select ug.id from tb_userGroups ug, tb_communities tc
where ug.community = tc.id and tc.rewriteURLParameter='o2') and is_default = true  and media_type='AUDIO' and subWeeks = 1 and tariff='_3G';


update tb_paymentPolicy
set is_default = true
where communityID=(select ug.id from tb_userGroups ug, tb_communities tc
where ug.community = tc.id and tc.rewriteURLParameter='o2') and is_default = false  and media_type='AUDIO' and subWeeks = 2  and tariff='_3G';

commit;