alter table tb_paymentDetails add column userId int(10) unsigned DEFAULT NULL;

-- Create MIG Payment Details
insert into tb_paymentDetails(
	paymentType,
	creationTimestampMillis,
	lastPaymentStatus,
	madeRetries,
	retriesOnError,
	disableTimestampMillis,
	paymentPolicyId,
	migPhoneNumber,
	activated,
	userId)
select
	"migSms" as paymentType,
	UNIX_TIMESTAMP(now())*1000 as creationTimestampMillis,
	"SUCCESSFUL" as lastPaymentStatus,
	0 as madeRetries,
	3 as retriesOnError,
	0 as disableTimestampMillis,
	tb_paymentPolicy.i as paymentPolicyId,
	concat(tb_operators.migName, ".", if(SUBSTRING(mobile,1,2)="07",concat("0044", SUBSTRING(mobile,2)), if(SUBSTRING(mobile,1,1)="+", concat("00", SUBSTRING(mobile,2)), mobile) ) ) as migPhoneNumber,
	if(paymentStatus=5, false, true) as activated,
	tb_users.i as userId
from tb_users, tb_paymentPolicy, tb_userGroups, tb_operators
where
	tb_users.userGroup=tb_userGroups.i
and paymentEnabled=true
and tb_users.paymentType="PSMS"
and ( paymentStatus in (2,5) or (paymentStatus=1 and tb_users.status=4))
and tb_userGroups.community=tb_paymentPolicy.communityID
and tb_paymentPolicy.paymentType="PSMS"
and tb_paymentPolicy.operator=tb_users.operator
and tb_operators.i=tb_users.operator;


-- Create Credit Card Payment Details
create table tmpSagePay as
	select i, useruid from tb_payments
		where paymentType='creditCard'
		and txType=8
		and status='OK'
		and useruid in ( select i from tb_users where paymenttype='creditCard' and paymentenabled=1 );

insert into tb_paymentDetails(
	paymentType,
	creationTimestampMillis,
	disableTimestampMillis,
	lastPaymentStatus,
	madeRetries,
	retriesOnError,
	VPSTxId,
	released,
	activated,
	vendorTxCode,
	securityKey,
	txAuthNo,
	userId)
select
	"sagePayCreditCard" as paymentType,
	UNIX_TIMESTAMP(now())*1000 as creationTimestampMillis,
	0 as disableTimestampMillis,
	"SUCCESSFUL" as lastPaymentStatus,
	0 as madeRetries,
	3 as retriesOnError,
	externalTxCode as VPSTxId,
	true as released,
	true as activated,
	internalTxCode as vendorTxCode,
	externalSecurityKey as securityKey,
	externalAuthCode as txAuthNo,
	userUID as userId
from tb_payments
where
	i in ( select max(i) from tmpSagePay group by useruid );

drop table tmpSagePay;

-- update payment policy for Sage Pay
update tb_paymentDetails set paymentPolicyId=
	(select tb_paymentPolicy.i from tb_paymentPolicy, tb_userGroups, tb_users where
		tb_paymentPolicy.paymentType='creditCard'
		and tb_paymentPolicy.communityID=tb_userGroups.community
		and tb_users.i = tb_paymentDetails.userId
		and tb_users.userGroup=tb_userGroups.i)
	where tb_paymentDetails.paymentType='sagePayCreditCard';


-- Create Paypal Payment Details
create table tmpPayPal as
	select i, useruid from tb_payments
		where paymentType='payPal'
		and txType=8
		and status='USER_CONFIRMED'
		and useruid in ( select i from tb_users where paymenttype='PAY_PAL' and paymentenabled=1 );

insert into tb_paymentDetails(
	paymentType,
	creationTimestampMillis,
	disableTimestampMillis,
	lastPaymentStatus,
	madeRetries,
	retriesOnError,
	activated,
	billingAgreementTxId,
	userId)
select
	"payPal" as paymentType,
	UNIX_TIMESTAMP(now())*1000 as creationTimestampMillis,
	0 as disableTimestampMillis,
	"SUCCESSFUL" as lastPaymentStatus,
	0 as madeRetries,
	3 as retriesOnError,
	true as activated,
	internalTxCode as billingAgreementTxId,
	userUID as userId
from tb_payments
where
	tb_payments.i in ( select max(i) from tmpPayPal group by useruid );

drop table tmpPayPal;

-- update payment policy for paypal
update tb_paymentDetails set paymentPolicyId=
	(select tb_paymentPolicy.i from tb_paymentPolicy, tb_userGroups, tb_users where
		tb_paymentPolicy.paymentType='PAY_PAL'
		and tb_paymentPolicy.communityID=tb_userGroups.community
		and tb_users.i = tb_paymentDetails.userId
		and tb_users.userGroup=tb_userGroups.i)
	where tb_paymentDetails.paymentType='payPal';
		

-- Update User with new payment details
update tb_users set currentPaymentDetailsId = (select tb_paymentDetails.i from tb_paymentDetails where tb_users.i=tb_paymentDetails.userId) where tb_users.i = (select tb_users.i from tb_paymentDetails where tb_users.i=tb_paymentDetails.userId);

alter table tb_paymentDetails drop column userId;