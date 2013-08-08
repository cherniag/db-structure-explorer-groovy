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

public class SubscriberPortImpl implements SubscriberPort{

	@Override
	@WebResult(name = "subscriberProfile", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
	@RequestWrapper(localName = "getSubscriberProfile", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata_2.GetSubscriberProfile")
	@WebMethod
	@ResponseWrapper(localName = "getSubscriberProfileResponse", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata_2.GetSubscriberProfileResponse")
	public SubscriberProfileType getSubscriberProfile(
			@WebParam(name = "subscriberID", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2") String subscriberID)
			throws GetSubscriberProfileFault {
		return getSubscriberProfileInternal(subscriberID);
	}

	private SubscriberProfileType getSubscriberProfileInternal(String subscriberID) {
		System.err.println("getSubscriber profile "+subscriberID);
		SubscriberProfileType s=new SubscriberProfileType();
		s.setOperator("o2");
		s.setSegment(SegmentType.CONSUMER);
		return s;
	}

	@Override
	@RequestWrapper(localName = "getSubscriberAndBillingSystem", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata_2.GetSubscriberAndBillingSystem")
	@WebMethod
	@ResponseWrapper(localName = "getSubscriberAndBillingSystemResponse", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata_2.GetSubscriberAndBillingSystemResponse")
	public void getSubscriberAndBillingSystem(
			@WebParam(name = "msisdn", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2") String msisdn,
			@WebParam(mode = Mode.OUT, name = "subscriberProfile", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2") Holder<uk.co.o2.soa.coredata_1.SubscriberProfileType> subscriberProfile,
			@WebParam(mode = Mode.OUT, name = "billingProfile", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2") Holder<BillingProfileType> billingProfile)
			throws GetSubscriberAndBillingSystemFault {
		// TODO Auto-generated method stub
		
	}

	@Override
	@WebResult(name = "chargingCategory", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
	@RequestWrapper(localName = "getChargingCategory", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata_2.GetChargingCategory")
	@WebMethod
	@ResponseWrapper(localName = "getChargingCategoryResponse", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata_2.GetChargingCategoryResponse")
	public List<String> getChargingCategory(
			@WebParam(name = "msisdn", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2") String msisdn,
			@WebParam(name = "productList", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2") ProductListType productList)
			throws GetChargingCategoryFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@WebResult(name = "serviceProviderDetails", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
	@RequestWrapper(localName = "getSPIDDetails_1", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata_2.GetSPIDDetails1RequestType")
	@WebMethod(operationName = "getSPIDDetails_1")
	@ResponseWrapper(localName = "getSPIDDetails_1Response", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata_2.GetSPIDDetails1ResponseType")
	public ServiceProviderDetailsType getSPIDDetails1(
			@WebParam(name = "serviceProviderId", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2") String serviceProviderId)
			throws GetSPIDDetails1Fault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	@WebResult(name = "getBillProfileResponse", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", partName = "getBillProfileResponse")
	@WebMethod
	public GetBillProfileResponse getBillProfile(
			@WebParam(partName = "getBillProfile", name = "getBillProfile", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2") GetBillProfile getBillProfile)
			throws GetBillProfileFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@WebResult(name = "Operator", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
	@RequestWrapper(localName = "getSubscriberOperator", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata_2.GetSubscriberOperator")
	@WebMethod
	@ResponseWrapper(localName = "getSubscriberOperatorResponse", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata_2.GetSubscriberOperatorResponse")
	public String getSubscriberOperator(
			@WebParam(name = "SubscriberID", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2") String subscriberID)
			throws GetSubscriberOperatorFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@WebResult(name = "billingProfile", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
	@RequestWrapper(localName = "getBillingSystem", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata_2.GetBillingSystem")
	@WebMethod
	@ResponseWrapper(localName = "getBillingSystemResponse", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata_2.GetBillingSystemResponse")
	public BillingProfileType getBillingSystem(
			@WebParam(name = "msisdn", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2") String msisdn)
			throws GetBillingSystemFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@WebResult(name = "channel", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
	@RequestWrapper(localName = "getSubscriberChannel", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata_2.GetSubscriberChannel")
	@WebMethod
	@ResponseWrapper(localName = "getSubscriberChannelResponse", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata_2.GetSubscriberChannelResponse")
	public String getSubscriberChannel(
			@WebParam(name = "SubscriberID", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2") String subscriberID)
			throws GetSubscriberChannelFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@WebResult(name = "PaymentCategory", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
	@RequestWrapper(localName = "getSubscriberPaymentCategory", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata_2.GetSubscriberPaymentCategory")
	@WebMethod
	@ResponseWrapper(localName = "getSubscriberPaymentCategoryResponse", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata_2.GetSubscriberPaymentCategoryResponse")
	public PaymentCategoryType getSubscriberPaymentCategory(
			@WebParam(name = "SubscriberID", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2") String subscriberID)
			throws GetSubscriberPaymentCategoryFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@WebResult(name = "Segment", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
	@RequestWrapper(localName = "getSubscriberSegment", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata_2.GetSubscriberSegment")
	@WebMethod
	@ResponseWrapper(localName = "getSubscriberSegmentResponse", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata_2.GetSubscriberSegmentResponse")
	public SegmentType getSubscriberSegment(
			@WebParam(name = "SubscriberID", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2") String subscriberID)
			throws GetSubscriberSegmentFault {
		// TODO Auto-generated method stub
		return null;
	}
	

}
