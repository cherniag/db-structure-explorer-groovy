insert into system (release_time_millis, version, release_name) values(unix_timestamp(now()), "5.4-SN", "5.4");

-- Update O2 PSMS server attempts and user unsubscribe flow
-- http://jira.musicqubed.com/browse/GO-321
-- Server MUST be shut down before script running

ALTER TABLE tb_paymentpolicy ADD advanced_payment_seconds INT UNSIGNED NOT NULL;
ALTER TABLE tb_paymentpolicy ADD after_next_sub_payment_seconds INT UNSIGNED NOT NULL;
ALTER TABLE tb_paymentpolicy ADD online bit NOT NULL;

update tb_paymentpolicy set advanced_payment_seconds=24*60*60 where communityID=10 and provider='NON_O2';
update tb_paymentpolicy set advanced_payment_seconds=24*60*60 where communityID=10 and provider='O2' and segment='CONSUMER' and paymentType='o2Psms';
update tb_paymentpolicy set advanced_payment_seconds=24*60*60 where communityID=10 and provider='O2' and segment='BUSINESS';

update tb_paymentpolicy set after_next_sub_payment_seconds=2*24*60*60*1000 where communityID=10 and provider='O2' and segment='CONSUMER' and paymentType='o2Psms' and contract='PAYG'; -- contract is NULL now
update tb_paymentpolicy set online=true;

ALTER TABLE tb_paymentDetails ADD made_attempts INT UNSIGNED NOT NULL default 0;

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
