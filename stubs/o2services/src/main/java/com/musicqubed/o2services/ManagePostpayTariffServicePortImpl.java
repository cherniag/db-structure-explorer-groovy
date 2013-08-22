package com.musicqubed.o2services;


import java.util.logging.Logger;

import o2stub.O2ServiceStub;

import uk.co.o2.soa.managepostpaytariffservice_2.CancelContractChangeFault;
import uk.co.o2.soa.managepostpaytariffservice_2.ChangeContractFault;
import uk.co.o2.soa.managepostpaytariffservice_2.GetBusinessPoliciesFault;
import uk.co.o2.soa.managepostpaytariffservice_2.GetContractFault;
import uk.co.o2.soa.managepostpaytariffservice_2.GetContractRevisionsFault;
import uk.co.o2.soa.managepostpaytariffservice_2.GetTariffCompatibleProductsFault;
import uk.co.o2.soa.managepostpaytariffservice_2.GetValidTariffsFault;
import uk.co.o2.soa.managepostpaytariffservice_2.ManagePostpayTariffPortType;

public class ManagePostpayTariffServicePortImpl implements ManagePostpayTariffPortType {

    private static final Logger LOG = Logger.getLogger(ManagePostpayTariffServicePortImpl.class.getName());

    public void getValidTariffs(uk.co.o2.soa.coredata_1.AccountType customerId,java.lang.Boolean overrideRules,java.util.List<java.lang.String> tariffFamily,javax.xml.ws.Holder<java.lang.String> msisdn,javax.xml.ws.Holder<uk.co.o2.soa.managepostpaytariffdata_2.ValidTariffsType> validTariffs) throws GetValidTariffsFault    { 
        LOG.info("Executing operation getValidTariffs");
        System.out.println(customerId);
        System.out.println(overrideRules);
        System.out.println(tariffFamily);
        try {
            java.lang.String msisdnValue = "";
            msisdn.value = msisdnValue;
            uk.co.o2.soa.managepostpaytariffdata_2.ValidTariffsType validTariffsValue = null;
            validTariffs.value = validTariffsValue;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new GetValidTariffsFault("getValidTariffsFault...");
    }

    public uk.co.o2.soa.managepostpaytariffdata_2.ServiceContractType getContract(uk.co.o2.soa.coredata_1.AccountType customerId) throws GetContractFault    { 
        LOG.info("Executing operation getContract");
        System.out.println(customerId);
        try {
            uk.co.o2.soa.managepostpaytariffdata_2.ServiceContractType _return = new O2ServiceStub().getManagePostpayContract(customerId.getMsisdn());
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new GetContractFault("getContractFault...");
    }

    public void changeContract(uk.co.o2.soa.coredata_1.AccountType customerId,uk.co.o2.soa.managepostpaytariffdata_2.ServiceContractType revisedContract,java.util.List<uk.co.o2.soa.pscommonpostpaydata_2.ProductType> additionalProducts,javax.xml.datatype.XMLGregorianCalendar effectiveStartDate,java.lang.Boolean overrideRules,javax.xml.ws.Holder<java.lang.String> statusCode,javax.xml.ws.Holder<java.lang.String> statusMessage,javax.xml.ws.Holder<java.lang.String> serviceRequestId,javax.xml.ws.Holder<java.lang.Boolean> isRulesOverridable) throws ChangeContractFault    { 
        LOG.info("Executing operation changeContract");
        System.out.println(customerId);
        System.out.println(revisedContract);
        System.out.println(additionalProducts);
        System.out.println(effectiveStartDate);
        System.out.println(overrideRules);
        try {
            java.lang.String statusCodeValue = "";
            statusCode.value = statusCodeValue;
            java.lang.String statusMessageValue = "";
            statusMessage.value = statusMessageValue;
            java.lang.String serviceRequestIdValue = "";
            serviceRequestId.value = serviceRequestIdValue;
            java.lang.Boolean isRulesOverridableValue = null;
            isRulesOverridable.value = isRulesOverridableValue;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new ChangeContractFault("changeContractFault...");
    }

    public void cancelContractChange(java.math.BigInteger cancelToken,javax.xml.ws.Holder<java.lang.String> statusCode,javax.xml.ws.Holder<java.lang.String> statusMessage,javax.xml.ws.Holder<java.lang.String> serviceRequestId,javax.xml.ws.Holder<java.lang.Boolean> isRulesOverridable) throws CancelContractChangeFault    { 
        LOG.info("Executing operation cancelContractChange");
        System.out.println(cancelToken);
        try {
            java.lang.String statusCodeValue = "";
            statusCode.value = statusCodeValue;
            java.lang.String statusMessageValue = "";
            statusMessage.value = statusMessageValue;
            java.lang.String serviceRequestIdValue = "";
            serviceRequestId.value = serviceRequestIdValue;
            java.lang.Boolean isRulesOverridableValue = null;
            isRulesOverridable.value = isRulesOverridableValue;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new CancelContractChangeFault("cancelContractChangeFault...");
    }

    public void getContractRevisions(uk.co.o2.soa.coredata_1.AccountType customerId,javax.xml.ws.Holder<java.lang.String> msisdn,javax.xml.ws.Holder<uk.co.o2.soa.managepostpaytariffdata_2.ServiceContractType> revisedContract,javax.xml.ws.Holder<javax.xml.datatype.XMLGregorianCalendar> effectiveDate,javax.xml.ws.Holder<java.math.BigInteger> cancelToken) throws GetContractRevisionsFault    { 
        LOG.info("Executing operation getContractRevisions");
        System.out.println(customerId);
        try {
            java.lang.String msisdnValue = "";
            msisdn.value = msisdnValue;
            uk.co.o2.soa.managepostpaytariffdata_2.ServiceContractType revisedContractValue = null;
            revisedContract.value = revisedContractValue;
            javax.xml.datatype.XMLGregorianCalendar effectiveDateValue = null;
            effectiveDate.value = effectiveDateValue;
            java.math.BigInteger cancelTokenValue = new java.math.BigInteger("0");
            cancelToken.value = cancelTokenValue;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new GetContractRevisionsFault("getContractRevisionsFault...");
    }

    public uk.co.o2.soa.managepostpaytariffdata_2.GetTariffCompatibleProductsResponse getTariffCompatibleProducts(uk.co.o2.soa.managepostpaytariffdata_2.GetTariffCompatibleProducts getTariffCompatibleProducts) throws GetTariffCompatibleProductsFault    { 
        LOG.info("Executing operation getTariffCompatibleProducts");
        System.out.println(getTariffCompatibleProducts);
        try {
            uk.co.o2.soa.managepostpaytariffdata_2.GetTariffCompatibleProductsResponse _return = null;
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new GetTariffCompatibleProductsFault("getTariffCompatibleProductsFault...");
    }

    public uk.co.o2.soa.managepostpaytariffdata_2.BusinessPoliciesType getBusinessPolicies(uk.co.o2.soa.coredata_1.AccountType customerId) throws GetBusinessPoliciesFault    { 
        LOG.info("Executing operation getBusinessPolicies");
        System.out.println(customerId);
        try {
            uk.co.o2.soa.managepostpaytariffdata_2.BusinessPoliciesType _return = null;
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new GetBusinessPoliciesFault("getBusinessPoliciesFault...");
    }

}
