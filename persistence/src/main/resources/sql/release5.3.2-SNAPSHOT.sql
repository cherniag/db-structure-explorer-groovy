start transaction;

update tb_promotions ps
set ps.isActive = 0
where ps.i = 23;
--HOW TO CALCULATE THIS ID:
--select ps.i from tb_promotions ps, tb_promoCode pc where pc.promotionId = ps.i and pc.code='promo8' and ps.userGroup = 10  and ps.freeWeeks = 8  and ps.isActive = 1
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed)
VALUES (53, 'O2promo4weeksAudio', 0, 0, 1356342064, 1606780800, 1, 4, 0, (select ug.id from tb_userGroups ug, tb_communities tc
where ug.community = tc.id and tc.rewriteURLParameter='o2'), 'PromoCode', 0, 'o2.promo.4weeks', false);
INSERT INTO tb_promoCode (code, promotionId, media_type)
VALUES ('o2.promo.4weeks', 53, 'AUDIO');



update tb_promotions ps
set ps.isActive = 0
where ps.i = 36;
--HOW TO CALCULATE THIS ID ON ENV:
--select ps.i from tb_promotions ps, tb_promoCode pc where pc.promotionId = ps.i and pc.code='o2.consumer.4g.paym.direct' and ps.userGroup = 10  and ps.freeWeeks = 8  and ps.isActive = 1
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed)
VALUES (54, 'O2promo4weeksVideo PAYM direct', 0, 0, 1388527200, 2147483647, 1, 4, 0, (select ug.id from tb_userGroups ug, tb_communities tc
where ug.community = tc.id and tc.rewriteURLParameter='o2'), 'PromoCode', 0, 'o2.consumer.4g.paym.direct.4weeks', false);
INSERT INTO tb_promoCode (code, promotionId, media_type)
VALUES ('o2.consumer.4g.paym.direct.4weeks', 54, 'VIDEO_AND_AUDIO');


update tb_promotions ps
set ps.isActive = 0
where ps.i = 37;
--HOW TO CALCULATE THIS ID:
--select ps.i from tb_promotions ps, tb_promoCode pc where pc.promotionId = ps.i and pc.code='o2.consumer.4g.paym.indirect' and ps.userGroup = 10  and ps.freeWeeks = 8  and ps.isActive = 1
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed)
VALUES (55, 'O2promo4weeksVideo PAYM indirect', 0, 0, 1377220905, 2147483647, 1, 4, 0, (select ug.id from tb_userGroups ug, tb_communities tc
where ug.community = tc.id and tc.rewriteURLParameter='o2'), 'PromoCode', 0, 'o2.consumer.4g.paym.indirect.4weeks', false);
INSERT INTO tb_promoCode (code, promotionId, media_type)
VALUES ('o2.consumer.4g.paym.indirect.4weeks', 55, 'VIDEO_AND_AUDIO');



update tb_promotions ps
set ps.isActive = 0
where ps.i = 38;
--HOW TO CALCULATE THIS ID:
--select ps.i from tb_promotions ps, tb_promoCode pc where pc.promotionId = ps.i and pc.code='o2.consumer.4g.payg' and ps.userGroup = 10  and ps.freeWeeks = 8  and ps.isActive = 1
INSERT INTO tb_promotions (i, description, numUsers, maxUsers, startDate, endDate, isActive, freeWeeks, subWeeks, userGroup, type, showPromotion, label, is_white_listed)
 VALUES (56, 'O2promo4weeksVideo PAYG', 0, 0, 1377220915, 2147483647, 1, 4, 0, (select ug.id from tb_userGroups ug, tb_communities tc
where ug.community = tc.id and tc.rewriteURLParameter='o2'), 'PromoCode', 0, 'o2.consumer.4g.payg.4weeks', false);
INSERT INTO tb_promoCode (code, promotionId, media_type)
VALUES ('o2.consumer.4g.payg.4weeks', 56, 'VIDEO_AND_AUDIO');



commit;