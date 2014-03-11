insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "5.4-SN", "5.4");

-- Update O2 PSMS server attempts and user unsubscribe flow
-- http://jira.musicqubed.com/browse/GO-321
-- Server MUST be shut down before script running

ALTER TABLE tb_paymentpolicy ADD advanced_payment_seconds INT UNSIGNED NOT NULL;
ALTER TABLE tb_paymentpolicy ADD after_next_sub_payment_seconds INT UNSIGNED NOT NULL;
ALTER TABLE tb_paymentpolicy ADD online bit NOT NULL;
ALTER TABLE tb_paymentDetails ADD made_attempts INT UNSIGNED NOT NULL default 0;

start transaction;

update tb_paymentpolicy set advanced_payment_seconds=24*60*60 where communityID=10 and provider='NON_O2';
-- update tb_paymentpolicy set advanced_payment_seconds=24*60*60 where communityID=10 and provider='O2' and segment='CONSUMER' and paymentType='o2Psms';
update tb_paymentpolicy set advanced_payment_seconds=24*60*60 where communityID=10 and provider='O2' and segment='BUSINESS';

update tb_paymentpolicy set after_next_sub_payment_seconds=2*24*60*60*1000 where communityID=10 and provider='O2' and segment='CONSUMER' and paymentType='o2Psms' and contract='PAYG'; -- contract is NULL now
update tb_paymentpolicy set online=true;

-- 3?
select count(*) from tb_paymentpolicy where communityID = 10 AND tariff = '_3G' AND paymentType = 'o2Psms' AND (segment = 'CONSUMER' or segment is null) AND
                                            (provider = 'O2' or provider is null) and online is true;

update tb_paymentpolicy set online=false where communityID = 10 AND tariff = '_3G' AND paymentType = 'o2Psms' AND (segment = 'CONSUMER' or segment is null) AND
(provider = 'O2' or provider is null);

INSERT INTO tb_paymentPolicy
(communityID, subWeeks, subCost, paymentType, operator, shortCode, currencyIso, availableInStore, app_store_product_id, contract, segment   , content_category, content_type          , content_description     , sub_merchant_id, provider, tariff, media_type, advanced_payment_seconds, after_next_sub_payment_seconds, is_default, online) VALUES
  (10         , 5       , '5'    , 'o2Psms'   , NULL    , ''       , 'GBP'      , true            , NULL                , 'PAYG'  , 'CONSUMER', 'other'         , 'mqbed_tracks_3107056', 'Description of content', 'O2 Tracks'    , 'O2'    , '_3G' , 'AUDIO'   ,24*60*60                 ,2*24*60*60                     , false     , true),
  (10         , 2       , '2'    , 'o2Psms'   , NULL    , ''       , 'GBP'      , true            , NULL                , 'PAYG'  , 'CONSUMER', 'other'         , 'mqbed_tracks_3107055', 'Description of content', 'O2 Tracks'    , 'O2'    , '_3G' , 'AUDIO'   ,24*60*60                 ,2*24*60*60                     , true      , true),
  (10         , 1       , '1'    , 'o2Psms'   , NULL    , ''       , 'GBP'      , true            , NULL                , 'PAYG'  , 'CONSUMER', 'other'         , 'mqbed_tracks_3107054', 'Description of content', 'O2 Tracks'    , 'O2'    , '_3G' , 'AUDIO'   ,24*60*60                 ,2*24*60*60                     , false     , true),
  (10         , 5       , '5'    , 'o2Psms'   , NULL    , ''       , 'GBP'      , true            , NULL                , 'PAYM'  , 'CONSUMER', 'other'         , 'mqbed_tracks_3107056', 'Description of content', 'O2 Tracks'    , 'O2'    , '_3G' , 'AUDIO'   ,24*60*60                 ,0                              , false     , true),
  (10         , 2       , '2'    , 'o2Psms'   , NULL    , ''       , 'GBP'      , true            , NULL                , 'PAYM'  , 'CONSUMER', 'other'         , 'mqbed_tracks_3107055', 'Description of content', 'O2 Tracks'    , 'O2'    , '_3G' , 'AUDIO'   ,24*60*60                 ,0                              , true      , true),
  (10         , 1       , '1'    , 'o2Psms'   , NULL    , ''       , 'GBP'      , true            , NULL                , 'PAYM'  , 'CONSUMER', 'other'         , 'mqbed_tracks_3107054', 'Description of content', 'O2 Tracks'    , 'O2'    , '_3G' , 'AUDIO'   ,24*60*60                 ,0                              , false     , true);

UPDATE
    tb_paymentDetails pd JOIN tb_users u
      ON pd.i = u.currentPaymentDetailsId JOIN tb_paymentPolicy ppol
      ON ppol.i = pd.paymentPolicyId
         AND ppol.communityID = 10
         AND ppol.tariff = '_3G'
         AND ppol.paymentType = 'o2Psms'
         AND(
      ppol.segment = 'CONSUMER'
      OR ppol.segment IS NULL
    )
         AND(
      ppol.provider = 'O2'
      OR ppol.provider IS NULL
    )
SET
  pd.paymentPolicyId =(
    SELECT
      pp.i
    FROM
      tb_paymentPolicy pp
    WHERE
      pp.subWeeks = ppol.subWeeks
      AND pp.contract = u.contract
      AND u.contract='PAYG'
      AND pp.communityID = 10
      AND pp.tariff = '_3G'
      AND pp.paymentType = 'o2Psms'
      AND(
        pp.segment = 'CONSUMER'
        OR pp.segment IS NULL
      )
      AND(
        pp.provider = 'O2'
        OR pp.provider IS NULL
      )
      AND pp.online IS TRUE
  )
WHERE
  (
    pd.lastPaymentStatus = 'NONE'
    OR pd.lastPaymentStatus = 'SUCCESSFUL'
  )
  AND pd.activated IS TRUE
;

update tb_paymentDetails pd
  join tb_users u
    on pd.i=u.currentPaymentDetailsId
  join tb_paymentPolicy pp
    on pp.i=pd.paymentPolicyId
set pd.made_attempts=1
where
  (pd.lastPaymentStatus='ERROR' or pd.lastPaymentStatus='EXTERNAL_ERROR')
  and pd.activated is true
  and pd.made_attempts!=pd.retriesOnError
  and u.nextSubPayment<UNIX_TIMESTAMP()
  and pp.advancedPaymentSeconds>0
  and u.lastDeviceLogin!=0;

commit;