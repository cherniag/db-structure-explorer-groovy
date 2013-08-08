package com.example.customerservice.server;

import java.math.BigInteger;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

import uk.co.o2.soa.coredata_1.AccountType;
import uk.co.o2.soa.coredata_1.ServiceResultType;
import uk.co.o2.soa.manageprepaytariffdata_2.SubscriberTariffType;
import uk.co.o2.soa.manageprepaytariffdata_2.TariffDetailType;
import uk.co.o2.soa.manageprepaytariffdata_2.TariffParameterType;
import uk.co.o2.soa.manageprepaytariffservice_2.ChangeTariff1Fault;
import uk.co.o2.soa.manageprepaytariffservice_2.ChangeTariffParameters1Fault;
import uk.co.o2.soa.manageprepaytariffservice_2.GetBusinessPolicies1Fault;
import uk.co.o2.soa.manageprepaytariffservice_2.GetCurrentAndPendingTariff1Fault;
import uk.co.o2.soa.manageprepaytariffservice_2.GetTariff1Fault;
import uk.co.o2.soa.manageprepaytariffservice_2.GetTariffParameters1Fault;
import uk.co.o2.soa.manageprepaytariffservice_2.GetValidTariffs1Fault;
import uk.co.o2.soa.manageprepaytariffservice_2.ManagePrepayTariffPort;
import uk.co.o2.soa.manageprepaytariffservice_2.ValidateParameters1Fault;
import uk.co.o2.soa.manageprepaytariffservice_2.ValidateTariff1Fault;
import uk.co.o2.soa.pscommonmanageprepaydata_1.BusinessPolicy;
import uk.co.o2.soa.pscommonmanageprepaydata_1.TariffType;

public class ManagePrepayTariffPortImpl implements ManagePrepayTariffPort{

	@Override
	@RequestWrapper(localName = "changeTariff_1", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2", className = "uk.co.o2.soa.manageprepaytariffdata_2.ChangeTariff1")
	@WebMethod(operationName = "changeTariff_1")
	@ResponseWrapper(localName = "changeTariff_1Response", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2", className = "uk.co.o2.soa.manageprepaytariffdata_2.ChangeTariff1Response")
	public void changeTariff1(
			@WebParam(name = "customerId", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") AccountType customerId,
			@WebParam(name = "tariffId", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") BigInteger tariffId,
			@WebParam(name = "addParameter", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") List<TariffParameterType> addParameter,
			@WebParam(name = "overrideRules", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Boolean overrideRules,
			@WebParam(mode = Mode.OUT, name = "successResult", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<ServiceResultType> successResult,
			@WebParam(mode = Mode.OUT, name = "startDate", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<XMLGregorianCalendar> startDate,
			@WebParam(mode = Mode.OUT, name = "invalidParameter", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<List<TariffParameterType>> invalidParameter,
			@WebParam(mode = Mode.OUT, name = "generateDateTime", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<XMLGregorianCalendar> generateDateTime)
			throws ChangeTariff1Fault {
		// TODO Auto-generated method stub
		
	}

	@Override
	@RequestWrapper(localName = "getValidTariffs_1", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2", className = "uk.co.o2.soa.manageprepaytariffdata_2.GetValidTariffs1")
	@WebMethod(operationName = "getValidTariffs_1")
	@ResponseWrapper(localName = "getValidTariffs_1Response", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2", className = "uk.co.o2.soa.manageprepaytariffdata_2.GetValidTariffs1Response")
	public void getValidTariffs1(
			@WebParam(name = "customerId", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") AccountType customerId,
			@WebParam(name = "tariffType", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") TariffType tariffType,
			@WebParam(mode = Mode.OUT, name = "tariffDetail", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<List<TariffDetailType>> tariffDetail,
			@WebParam(mode = Mode.OUT, name = "generateDateTime", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<XMLGregorianCalendar> generateDateTime)
			throws GetValidTariffs1Fault {
		// TODO Auto-generated method stub
		
	}

	@Override
	@RequestWrapper(localName = "validateParameters_1", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2", className = "uk.co.o2.soa.manageprepaytariffdata_2.ValidateParameters1")
	@WebMethod(operationName = "validateParameters_1")
	@ResponseWrapper(localName = "validateParameters_1Response", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2", className = "uk.co.o2.soa.manageprepaytariffdata_2.ValidateParameters1Response")
	public void validateParameters1(
			@WebParam(name = "customerId", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") AccountType customerId,
			@WebParam(name = "validateParameter", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") List<TariffParameterType> validateParameter,
			@WebParam(mode = Mode.OUT, name = "validateParameterResult", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<ServiceResultType> validateParameterResult,
			@WebParam(mode = Mode.OUT, name = "invalidParameter", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<List<TariffParameterType>> invalidParameter,
			@WebParam(mode = Mode.OUT, name = "generateDateTime", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<XMLGregorianCalendar> generateDateTime)
			throws ValidateParameters1Fault {
		// TODO Auto-generated method stub
		
	}

	@Override
	@RequestWrapper(localName = "getBusinessPolicies_1", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2", className = "uk.co.o2.soa.manageprepaytariffdata_2.GetBusinessPolicies1")
	@WebMethod(operationName = "getBusinessPolicies_1")
	@ResponseWrapper(localName = "getBusinessPolicies_1Response", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2", className = "uk.co.o2.soa.manageprepaytariffdata_2.GetBusinessPolicies1Response")
	public void getBusinessPolicies1(
			@WebParam(name = "customerId", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") AccountType customerId,
			@WebParam(mode = Mode.OUT, name = "businessPolicies", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<List<BusinessPolicy>> businessPolicies,
			@WebParam(mode = Mode.OUT, name = "generateDateTime", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<XMLGregorianCalendar> generateDateTime)
			throws GetBusinessPolicies1Fault {
		// TODO Auto-generated method stub
		
	}

	@Override
	@RequestWrapper(localName = "getTariff_1", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2", className = "uk.co.o2.soa.manageprepaytariffdata_2.GetTariff1")
	@WebMethod(operationName = "getTariff_1")
	@ResponseWrapper(localName = "getTariff_1Response", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2", className = "uk.co.o2.soa.manageprepaytariffdata_2.GetTariff1Response")
	public void getTariff1(
			@WebParam(name = "customerId", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") AccountType customerId,
			@WebParam(mode = Mode.OUT, name = "accountBalance", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<Long> accountBalance,
			@WebParam(mode = Mode.OUT, name = "accountStatusDescription", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<String> accountStatusDescription,
			@WebParam(mode = Mode.OUT, name = "currentTariff", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<SubscriberTariffType> currentTariff,
			@WebParam(mode = Mode.OUT, name = "generateDateTime", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<XMLGregorianCalendar> generateDateTime)
			throws GetTariff1Fault {
		// TODO Auto-generated method stub
		
	}

	@Override
	@RequestWrapper(localName = "changeTariffParameters_1", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2", className = "uk.co.o2.soa.manageprepaytariffdata_2.ChangeTariffParameters1")
	@WebMethod(operationName = "changeTariffParameters_1")
	@ResponseWrapper(localName = "changeTariffParameters_1Response", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2", className = "uk.co.o2.soa.manageprepaytariffdata_2.ChangeTariffParameters1Response")
	public void changeTariffParameters1(
			@WebParam(name = "customerId", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") AccountType customerId,
			@WebParam(name = "tariffId", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") BigInteger tariffId,
			@WebParam(name = "deleteParameter", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") List<TariffParameterType> deleteParameter,
			@WebParam(name = "addParameter", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") List<TariffParameterType> addParameter,
			@WebParam(name = "overrideRules", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Boolean overrideRules,
			@WebParam(mode = Mode.OUT, name = "successResult", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<ServiceResultType> successResult,
			@WebParam(mode = Mode.OUT, name = "invalidParameter", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<List<TariffParameterType>> invalidParameter,
			@WebParam(mode = Mode.OUT, name = "generateDateTime", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<XMLGregorianCalendar> generateDateTime)
			throws ChangeTariffParameters1Fault {
		// TODO Auto-generated method stub
		
	}

	@Override
	@RequestWrapper(localName = "validateTariff_1", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2", className = "uk.co.o2.soa.manageprepaytariffdata_2.ValidateTariff1")
	@WebMethod(operationName = "validateTariff_1")
	@ResponseWrapper(localName = "validateTariff_1Response", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2", className = "uk.co.o2.soa.manageprepaytariffdata_2.ValidateTariff1Response")
	public void validateTariff1(
			@WebParam(name = "customerId", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") AccountType customerId,
			@WebParam(name = "tariffId", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") BigInteger tariffId,
			@WebParam(name = "validateParameter", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") List<TariffParameterType> validateParameter,
			@WebParam(mode = Mode.OUT, name = "validateParameterResult", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<ServiceResultType> validateParameterResult,
			@WebParam(mode = Mode.OUT, name = "invalidParameter", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<List<TariffParameterType>> invalidParameter,
			@WebParam(mode = Mode.OUT, name = "generateDateTime", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<XMLGregorianCalendar> generateDateTime)
			throws ValidateTariff1Fault {
		// TODO Auto-generated method stub
		
	}

	@Override
	@RequestWrapper(localName = "getCurrentAndPendingTariff_1", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2", className = "uk.co.o2.soa.manageprepaytariffdata_2.GetCurrentAndPendingTariff1")
	@WebMethod(operationName = "getCurrentAndPendingTariff_1")
	@ResponseWrapper(localName = "getCurrentAndPendingTariff_1Response", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2", className = "uk.co.o2.soa.manageprepaytariffdata_2.GetCurrentAndPendingTariff1Response")
	public void getCurrentAndPendingTariff1(
			@WebParam(name = "customerId", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") AccountType customerId,
			@WebParam(mode = Mode.OUT, name = "accountBalance", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<Long> accountBalance,
			@WebParam(mode = Mode.OUT, name = "accountStatusDescription", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<String> accountStatusDescription,
			@WebParam(mode = Mode.OUT, name = "currentTariff", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<SubscriberTariffType> currentTariff,
			@WebParam(mode = Mode.OUT, name = "pendingTariff", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<SubscriberTariffType> pendingTariff,
			@WebParam(mode = Mode.OUT, name = "generateDateTime", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<XMLGregorianCalendar> generateDateTime)
			throws GetCurrentAndPendingTariff1Fault {
		// TODO Auto-generated method stub
		
	}

	@Override
	@RequestWrapper(localName = "getTariffParameters_1", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2", className = "uk.co.o2.soa.manageprepaytariffdata_2.GetTariffParameters1")
	@WebMethod(operationName = "getTariffParameters_1")
	@ResponseWrapper(localName = "getTariffParameters_1Response", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2", className = "uk.co.o2.soa.manageprepaytariffdata_2.GetTariffParameters1Response")
	public void getTariffParameters1(
			@WebParam(name = "customerId", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") AccountType customerId,
			@WebParam(name = "tariffid", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") BigInteger tariffid,
			@WebParam(mode = Mode.OUT, name = "tariffParameter", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<List<TariffParameterType>> tariffParameter,
			@WebParam(mode = Mode.OUT, name = "generateDateTime", targetNamespace = "http://soa.o2.co.uk/manageprepaytariffdata_2") Holder<XMLGregorianCalendar> generateDateTime)
			throws GetTariffParameters1Fault {
		// TODO Auto-generated method stub
		
	}

}
