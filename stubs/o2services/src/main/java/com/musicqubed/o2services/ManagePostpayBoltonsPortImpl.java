package com.musicqubed.o2services;


import java.util.logging.Logger;

import o2stub.O2ServiceStub;

import uk.co.o2.soa.managepostpayboltonsservice_2.AddBoltonsFault;
import uk.co.o2.soa.managepostpayboltonsservice_2.CancelBoltonsChangeFault;
import uk.co.o2.soa.managepostpayboltonsservice_2.ChangeBoltonParametersFault;
import uk.co.o2.soa.managepostpayboltonsservice_2.GetBusinessPoliciesFault;
import uk.co.o2.soa.managepostpayboltonsservice_2.GetCurrentAndPendingBoltonsFault;
import uk.co.o2.soa.managepostpayboltonsservice_2.GetCurrentBoltonsFault;
import uk.co.o2.soa.managepostpayboltonsservice_2.GetValidBoltonsFault;
import uk.co.o2.soa.managepostpayboltonsservice_2.ManagePostpayBoltonsPortType;
import uk.co.o2.soa.managepostpayboltonsservice_2.RemoveBoltonsFault;

public class ManagePostpayBoltonsPortImpl implements ManagePostpayBoltonsPortType {

	private static final Logger LOG = Logger.getLogger(ManagePostpayBoltonsPortImpl.class.getName());

	public void getCurrentBoltons(uk.co.o2.soa.coredata_1.AccountType customerId,
			javax.xml.ws.Holder<java.lang.String> msisdn,
			javax.xml.ws.Holder<uk.co.o2.soa.managepostpayboltonsdata_2.MyCurrentBoltonsType> myCurrentBoltons)
			throws GetCurrentBoltonsFault {
		LOG.info("Executing operation getCurrentBoltons");
		System.out.println(customerId);
		try {
			java.lang.String msisdnValue = "";
			msisdn.value = msisdnValue;
			myCurrentBoltons.value = new O2ServiceStub().getCurrentBoltonsPostPay(msisdn);
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	
	public void changeBoltonParameters(uk.co.o2.soa.coredata_1.AccountType customerId,
			java.util.List<uk.co.o2.soa.managepostpayboltonsdata_2.ChangeBoltonType> changeBolton,
			javax.xml.ws.Holder<java.util.List<uk.co.o2.soa.managepostpayboltonsdata_2.Results>> changeResults,
			javax.xml.ws.Holder<java.lang.Boolean> isRulesOverridable) throws ChangeBoltonParametersFault {
		LOG.info("Executing operation changeBoltonParameters");
		System.out.println(customerId);
		System.out.println(changeBolton);
		try {
			java.util.List<uk.co.o2.soa.managepostpayboltonsdata_2.Results> changeResultsValue = null;
			changeResults.value = changeResultsValue;
			java.lang.Boolean isRulesOverridableValue = null;
			isRulesOverridable.value = isRulesOverridableValue;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		//throw new ChangeBoltonParametersFault("changeBoltonParametersFault...");
	}

	public uk.co.o2.soa.managepostpayboltonsdata_2.BusinessPoliciesType getBusinessPolicies(
			uk.co.o2.soa.coredata_1.AccountType customerId) throws GetBusinessPoliciesFault {
		LOG.info("Executing operation getBusinessPolicies");
		System.out.println(customerId);
		try {
			uk.co.o2.soa.managepostpayboltonsdata_2.BusinessPoliciesType _return = null;
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		//throw new GetBusinessPoliciesFault("getBusinessPoliciesFault...");
	}

	public uk.co.o2.soa.managepostpayboltonsdata_2.CancelResultType cancelBoltonsChange(
			uk.co.o2.soa.managepostpayboltonsdata_2.CancelDetails cancelDetails) throws CancelBoltonsChangeFault {
		LOG.info("Executing operation cancelBoltonsChange");
		System.out.println(cancelDetails);
		try {
			uk.co.o2.soa.managepostpayboltonsdata_2.CancelResultType _return = null;
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		//throw new CancelBoltonsChangeFault("cancelBoltonsChangeFault...");
	}

	public void getCurrentAndPendingBoltons(uk.co.o2.soa.coredata_1.AccountType customerId,
			javax.xml.ws.Holder<java.lang.String> msisdn,
			javax.xml.ws.Holder<uk.co.o2.soa.managepostpayboltonsdata_2.MyBoltonsType> myBoltons)
			throws GetCurrentAndPendingBoltonsFault {
		LOG.info("Executing operation getCurrentAndPendingBoltons");
		System.out.println(customerId);
		try {
			java.lang.String msisdnValue = "";
			msisdn.value = msisdnValue;
			uk.co.o2.soa.managepostpayboltonsdata_2.MyBoltonsType myBoltonsValue = null;
			myBoltons.value = myBoltonsValue;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		//throw new GetCurrentAndPendingBoltonsFault("getCurrentAndPendingBoltonsFault...");
	}


	public void removeBoltons(uk.co.o2.soa.coredata_1.AccountType customerId,
			java.util.List<uk.co.o2.soa.managepostpayboltonsdata_2.RemovalBoltonType> removalBolton,
			java.lang.Boolean overrideRules,
			javax.xml.ws.Holder<java.util.List<uk.co.o2.soa.managepostpayboltonsdata_2.Results>> changeResults,
			javax.xml.ws.Holder<java.lang.Boolean> isRulesOverridable) throws RemoveBoltonsFault {
		LOG.info("Executing operation removeBoltons");
		System.out.println(customerId);
		System.out.println(removalBolton);
		System.out.println(overrideRules);
		try {
			java.util.List<uk.co.o2.soa.managepostpayboltonsdata_2.Results> changeResultsValue = null;
			changeResults.value = changeResultsValue;
			java.lang.Boolean isRulesOverridableValue = null;
			isRulesOverridable.value = isRulesOverridableValue;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		//throw new RemoveBoltonsFault("removeBoltonsFault...");
	}

	public void addBoltons(uk.co.o2.soa.coredata_1.AccountType customerId,
			java.util.List<uk.co.o2.soa.managepostpayboltonsdata_2.AdditionalBoltonType> additionalBolton,
			java.lang.Boolean overrideRules,
			javax.xml.ws.Holder<java.util.List<uk.co.o2.soa.managepostpayboltonsdata_2.Results>> changeResults,
			javax.xml.ws.Holder<java.lang.Boolean> isRulesOverridable) throws AddBoltonsFault {
		LOG.info("Executing operation addBoltons");
		System.out.println(customerId);
		System.out.println(additionalBolton);
		System.out.println(overrideRules);
		try {
			java.util.List<uk.co.o2.soa.managepostpayboltonsdata_2.Results> changeResultsValue = null;
			changeResults.value = changeResultsValue;
			java.lang.Boolean isRulesOverridableValue = null;
			isRulesOverridable.value = isRulesOverridableValue;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		//throw new AddBoltonsFault("addBoltonsFault...");
	}

	public uk.co.o2.soa.managepostpayboltonsdata_2.GetValidBoltonsResponse getValidBoltons(
			uk.co.o2.soa.managepostpayboltonsdata_2.GetValidBoltons getValidBoltons) throws GetValidBoltonsFault {
		LOG.info("Executing operation getValidBoltons");
		System.out.println(getValidBoltons);
		try {
			uk.co.o2.soa.managepostpayboltonsdata_2.GetValidBoltonsResponse _return = null;
			return _return;
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		//throw new GetValidBoltonsFault("getValidBoltonsFault...");
	}

}
