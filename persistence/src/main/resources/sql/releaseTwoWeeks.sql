 -- Final insert of the release version
-- insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "4.1-SN", "4.1-SN");

 -- delete from tb_promotions where description="TwoWeeksOnSubscription";
 
 INSERT INTO tb_promotions(
  description
  ,numUsers
  ,maxUsers
  ,startDate
  ,endDate
  ,isActive
  ,freeWeeks
  ,subWeeks
  ,userGroup
  ,type
  ,showPromotion
  ,label
  ,is_white_listed
) VALUES (
  'TwoWeeksOnSubscription' -- description - IN char(100)
  ,0 -- numUsers - IN int(10) unsigned
  ,0 -- maxUsers - IN int(10) unsigned
  ,UNIX_TIMESTAMP('2013-10-19') -- startDate - IN int(10) unsigned
  ,UNIX_TIMESTAMP('2013-11-19') -- endDate - IN int(10) unsigned
  ,1 -- isActive - IN tinyint(1)
  ,2 -- freeWeeks - IN tinyint(3) unsigned
  ,0 -- subWeeks - IN tinyint(3) unsigned
  ,10 -- userGroup - IN tinyint(3) unsigned
  ,'PromoCode' -- type - IN char(20)
  ,0 -- showPromotion - IN tinyint(1)
  ,'TwoWeeksOnSubscription'  -- label - IN varchar(50)
  ,0   -- is_white_listed - IN bit(1)
);

INSERT INTO tb_promoCode(
  code
  ,promotionId
  ,media_type
) VALUES (
  'TwoWeeksOnSubscription' -- code - IN varchar(255)
  , (SELECT i FROM tb_promotions where label='TwoWeeksOnSubscription')  -- promotionId - IN int(11)
  ,'AUDIO' -- media_type - IN char(255)
);
