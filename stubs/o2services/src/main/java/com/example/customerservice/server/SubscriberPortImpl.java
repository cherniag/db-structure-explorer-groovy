package com.example.customerservice.server;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import javax.jws.WebResult;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

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

import com.example.customerservice.server.impl.O2ServiceStub;

public class SubscriberPortImpl implements SubscriberPort {

	@Override
	public SubscriberProfileType getSubscriberProfile(String subscriberID) throws GetSubscriberProfileFault {
		return new O2ServiceStub().getSubscriberProfileInternal(subscriberID);
	}

	@Override
	public void getSubscriberAndBillingSystem(
			String msisdn,
			Holder<uk.co.o2.soa.coredata_1.SubscriberProfileType> subscriberProfile,
			Holder<BillingProfileType> billingProfile)
			throws GetSubscriberAndBillingSystemFault {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getChargingCategory(String msisdn, ProductListType productList) throws GetChargingCategoryFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceProviderDetailsType getSPIDDetails1(String serviceProviderId) throws GetSPIDDetails1Fault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GetBillProfileResponse getBillProfile(GetBillProfile getBillProfile) throws GetBillProfileFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSubscriberOperator(String subscriberID) throws GetSubscriberOperatorFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BillingProfileType getBillingSystem(String msisdn) throws GetBillingSystemFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSubscriberChannel(String subscriberID) throws GetSubscriberChannelFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentCategoryType getSubscriberPaymentCategory(String subscriberID)
			throws GetSubscriberPaymentCategoryFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SegmentType getSubscriberSegment(String subscriberID) throws GetSubscriberSegmentFault {
		// TODO Auto-generated method stub
		return null;
	}

}
