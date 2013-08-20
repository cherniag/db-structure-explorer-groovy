package com.musicqubed.o2services;

import java.util.List;

import javax.xml.ws.Holder;

import o2stub.O2ServiceStub;
import uk.co.o2.soa.coredata_1.PaymentCategoryType;
import uk.co.o2.soa.coredata_1.SegmentType;
import uk.co.o2.soa.subscriberdata_2.BillingProfileType;
import uk.co.o2.soa.subscriberdata_2.GetBillProfile;
import uk.co.o2.soa.subscriberdata_2.GetBillProfileResponse;
import uk.co.o2.soa.subscriberdata_2.ProductListType;
import uk.co.o2.soa.subscriberdata_2.ServiceProviderDetailsType;
import uk.co.o2.soa.subscriberdata_2.SubscriberProfileType;
import uk.co.o2.soa.subscriberservice_2.GetBillProfileFault;
import uk.co.o2.soa.subscriberservice_2.GetBillingSystemFault;
import uk.co.o2.soa.subscriberservice_2.GetChargingCategoryFault;
import uk.co.o2.soa.subscriberservice_2.GetSPIDDetails1Fault;
import uk.co.o2.soa.subscriberservice_2.GetSubscriberAndBillingSystemFault;
import uk.co.o2.soa.subscriberservice_2.GetSubscriberChannelFault;
import uk.co.o2.soa.subscriberservice_2.GetSubscriberOperatorFault;
import uk.co.o2.soa.subscriberservice_2.GetSubscriberPaymentCategoryFault;
import uk.co.o2.soa.subscriberservice_2.GetSubscriberProfileFault;
import uk.co.o2.soa.subscriberservice_2.GetSubscriberSegmentFault;
import uk.co.o2.soa.subscriberservice_2.SubscriberPort;

public class SubscriberPortImpl implements SubscriberPort {

	@Override
	public SubscriberProfileType getSubscriberProfile(String subscriberID) throws GetSubscriberProfileFault {
		return new O2ServiceStub().getSubscriberProfileInternal(subscriberID);
	}

	@Override
	public void getSubscriberAndBillingSystem(String msisdn,
			Holder<uk.co.o2.soa.coredata_1.SubscriberProfileType> subscriberProfile,
			Holder<BillingProfileType> billingProfile) throws GetSubscriberAndBillingSystemFault {

	}

	@Override
	public List<String> getChargingCategory(String msisdn, ProductListType productList) throws GetChargingCategoryFault {

		return null;
	}

	@Override
	public ServiceProviderDetailsType getSPIDDetails1(String serviceProviderId) throws GetSPIDDetails1Fault {

		return null;
	}

	@Override
	public GetBillProfileResponse getBillProfile(GetBillProfile getBillProfile) throws GetBillProfileFault {

		return null;
	}

	@Override
	public String getSubscriberOperator(String subscriberID) throws GetSubscriberOperatorFault {

		return null;
	}

	@Override
	public BillingProfileType getBillingSystem(String msisdn) throws GetBillingSystemFault {

		return null;
	}

	@Override
	public String getSubscriberChannel(String subscriberID) throws GetSubscriberChannelFault {

		return null;
	}

	@Override
	public PaymentCategoryType getSubscriberPaymentCategory(String subscriberID)
			throws GetSubscriberPaymentCategoryFault {

		return null;
	}

	@Override
	public SegmentType getSubscriberSegment(String subscriberID) throws GetSubscriberSegmentFault {

		return null;
	}

}
