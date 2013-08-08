package com.example.customerservice.server;

import java.math.BigInteger;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import uk.co.o2.soa.chargecustomerdata_1.ServiceResult;
import uk.co.o2.soa.chargecustomerservice_1.BillSubscriberFault;
import uk.co.o2.soa.chargecustomerservice_1.ChargeCustomerPort;

public class ChargeCustomerPortImpl implements ChargeCustomerPort{

	@Override
	@WebResult(name = "result", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1")
	@RequestWrapper(localName = "billSubscriber", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1", className = "uk.co.o2.soa.chargecustomerdata_1.BillSubscriber")
	@WebMethod
	@ResponseWrapper(localName = "billSubscriberResponse", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1", className = "uk.co.o2.soa.chargecustomerdata_1.BillSubscriberResponse")
	public ServiceResult billSubscriber(
			@WebParam(name = "msisdn", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1") String msisdn,
			@WebParam(name = "subMerchantId", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1") String subMerchantId,
			@WebParam(name = "priceGross", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1") BigInteger priceGross,
			@WebParam(name = "priceNet", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1") BigInteger priceNet,
			@WebParam(name = "debitCredit", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1") String debitCredit,
			@WebParam(name = "contentCategory", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1") String contentCategory,
			@WebParam(name = "contentType", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1") String contentType,
			@WebParam(name = "contentDescription", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1") String contentDescription,
			@WebParam(name = "applicationReference", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1") String applicationReference,
			@WebParam(name = "smsNotify", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1") boolean smsNotify,
			@WebParam(name = "smsMessage", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1") String smsMessage,
			@WebParam(name = "promotionCode", targetNamespace = "http://soa.o2.co.uk/chargecustomerdata_1") String promotionCode)
			throws BillSubscriberFault {
		// TODO Auto-generated method stub
		return null;
	}

}
