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

import uk.co.o2.soa.coredata_1.AccountType;
import uk.co.o2.soa.managepostpayboltonsdata_2.AdditionalBoltonType;
import uk.co.o2.soa.managepostpayboltonsdata_2.BusinessPoliciesType;
import uk.co.o2.soa.managepostpayboltonsdata_2.CancelDetails;
import uk.co.o2.soa.managepostpayboltonsdata_2.CancelResultType;
import uk.co.o2.soa.managepostpayboltonsdata_2.ChangeBoltonType;
import uk.co.o2.soa.managepostpayboltonsdata_2.GetValidBoltons;
import uk.co.o2.soa.managepostpayboltonsdata_2.GetValidBoltonsResponse;
import uk.co.o2.soa.managepostpayboltonsdata_2.MyBoltonsType;
import uk.co.o2.soa.managepostpayboltonsdata_2.MyCurrentBoltonsType;
import uk.co.o2.soa.managepostpayboltonsdata_2.RemovalBoltonType;
import uk.co.o2.soa.managepostpayboltonsdata_2.Results;
import uk.co.o2.soa.managepostpayboltonsservice_2.AddBoltonsFault;
import uk.co.o2.soa.managepostpayboltonsservice_2.CancelBoltonsChangeFault;
import uk.co.o2.soa.managepostpayboltonsservice_2.ChangeBoltonParametersFault;
import uk.co.o2.soa.managepostpayboltonsservice_2.GetBusinessPoliciesFault;
import uk.co.o2.soa.managepostpayboltonsservice_2.GetCurrentAndPendingBoltonsFault;
import uk.co.o2.soa.managepostpayboltonsservice_2.GetCurrentBoltonsFault;
import uk.co.o2.soa.managepostpayboltonsservice_2.GetValidBoltonsFault;
import uk.co.o2.soa.managepostpayboltonsservice_2.ManagePostpayBoltonsPortType;
import uk.co.o2.soa.managepostpayboltonsservice_2.RemoveBoltonsFault;

public class ManagePostpayBoltonsPortImpl implements ManagePostpayBoltonsPortType{

	@Override
	@RequestWrapper(localName = "changeBoltonParameters", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2", className = "uk.co.o2.soa.managepostpayboltonsdata_2.ChangeBoltonParameters")
	@WebMethod
	@ResponseWrapper(localName = "changeBoltonParametersResponse", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2", className = "uk.co.o2.soa.managepostpayboltonsdata_2.GenericBoltonResponse")
	public void changeBoltonParameters(
			@WebParam(name = "customerId", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") AccountType customerId,
			@WebParam(name = "changeBolton", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") List<ChangeBoltonType> changeBolton,
			@WebParam(mode = Mode.OUT, name = "changeResults", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") Holder<List<Results>> changeResults,
			@WebParam(mode = Mode.OUT, name = "isRulesOverridable", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") Holder<Boolean> isRulesOverridable)
			throws ChangeBoltonParametersFault {
		// TODO Auto-generated method stub
		
	}

	@Override
	@WebResult(name = "businessPolicies", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2")
	@RequestWrapper(localName = "getBusinessPolicies", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2", className = "uk.co.o2.soa.managepostpayboltonsdata_2.GetBusinessPolicies")
	@WebMethod
	@ResponseWrapper(localName = "getBusinessPoliciesResponse", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2", className = "uk.co.o2.soa.managepostpayboltonsdata_2.GetBusinessPoliciesResponse")
	public BusinessPoliciesType getBusinessPolicies(
			@WebParam(name = "customerId", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") AccountType customerId)
			throws GetBusinessPoliciesFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@WebResult(name = "cancelResult", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2")
	@RequestWrapper(localName = "cancelBoltonsChange", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2", className = "uk.co.o2.soa.managepostpayboltonsdata_2.CancelBoltonsChange")
	@WebMethod
	@ResponseWrapper(localName = "cancelBoltonsChangeResponse", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2", className = "uk.co.o2.soa.managepostpayboltonsdata_2.CancelBoltonsChangeResponse")
	public CancelResultType cancelBoltonsChange(
			@WebParam(name = "cancelDetails", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") CancelDetails cancelDetails)
			throws CancelBoltonsChangeFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@RequestWrapper(localName = "getCurrentAndPendingBoltons", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2", className = "uk.co.o2.soa.managepostpayboltonsdata_2.GetCurrentAndPendingBoltons")
	@WebMethod
	@ResponseWrapper(localName = "getCurrentAndPendingBoltonsResponse", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2", className = "uk.co.o2.soa.managepostpayboltonsdata_2.GetCurrentAndPendingBoltonsResponse")
	public void getCurrentAndPendingBoltons(
			@WebParam(name = "customerId", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") AccountType customerId,
			@WebParam(mode = Mode.OUT, name = "MSISDN", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") Holder<String> msisdn,
			@WebParam(mode = Mode.OUT, name = "myBoltons", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") Holder<MyBoltonsType> myBoltons)
			throws GetCurrentAndPendingBoltonsFault {
		// TODO Auto-generated method stub
		
	}

	@Override
	@RequestWrapper(localName = "getCurrentBoltons", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2", className = "uk.co.o2.soa.managepostpayboltonsdata_2.GetCurrentBoltons")
	@WebMethod
	@ResponseWrapper(localName = "getCurrentBoltonsResponse", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2", className = "uk.co.o2.soa.managepostpayboltonsdata_2.GetCurrentBoltonsResponse")
	public void getCurrentBoltons(
			@WebParam(name = "customerId", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") AccountType customerId,
			@WebParam(mode = Mode.OUT, name = "MSISDN", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") Holder<String> msisdn,
			@WebParam(mode = Mode.OUT, name = "myCurrentBoltons", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") Holder<MyCurrentBoltonsType> myCurrentBoltons)
			throws GetCurrentBoltonsFault {
		// TODO Auto-generated method stub
		
	}

	@Override
	@RequestWrapper(localName = "removeBoltons", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2", className = "uk.co.o2.soa.managepostpayboltonsdata_2.RemoveBoltons")
	@WebMethod
	@ResponseWrapper(localName = "removeBoltonsResponse", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2", className = "uk.co.o2.soa.managepostpayboltonsdata_2.GenericBoltonResponse")
	public void removeBoltons(
			@WebParam(name = "customerId", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") AccountType customerId,
			@WebParam(name = "removalBolton", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") List<RemovalBoltonType> removalBolton,
			@WebParam(name = "overrideRules", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") Boolean overrideRules,
			@WebParam(mode = Mode.OUT, name = "changeResults", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") Holder<List<Results>> changeResults,
			@WebParam(mode = Mode.OUT, name = "isRulesOverridable", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") Holder<Boolean> isRulesOverridable)
			throws RemoveBoltonsFault {
		// TODO Auto-generated method stub
		
	}

	@Override
	@RequestWrapper(localName = "addBoltons", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2", className = "uk.co.o2.soa.managepostpayboltonsdata_2.AddBoltons")
	@WebMethod
	@ResponseWrapper(localName = "addBoltonsResponse", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2", className = "uk.co.o2.soa.managepostpayboltonsdata_2.GenericBoltonResponse")
	public void addBoltons(
			@WebParam(name = "customerId", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") AccountType customerId,
			@WebParam(name = "additionalBolton", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") List<AdditionalBoltonType> additionalBolton,
			@WebParam(name = "overrideRules", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") Boolean overrideRules,
			@WebParam(mode = Mode.OUT, name = "changeResults", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") Holder<List<Results>> changeResults,
			@WebParam(mode = Mode.OUT, name = "isRulesOverridable", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") Holder<Boolean> isRulesOverridable)
			throws AddBoltonsFault {
		// TODO Auto-generated method stub
		
	}

	@Override
	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	@WebResult(name = "getValidBoltonsResponse", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2", partName = "getValidBoltonsResponse")
	@WebMethod
	public GetValidBoltonsResponse getValidBoltons(
			@WebParam(partName = "getValidBoltons", name = "getValidBoltons", targetNamespace = "http://soa.o2.co.uk/managepostpayboltonsdata_2") GetValidBoltons getValidBoltons)
			throws GetValidBoltonsFault {
		// TODO Auto-generated method stub
		return null;
	}

}
