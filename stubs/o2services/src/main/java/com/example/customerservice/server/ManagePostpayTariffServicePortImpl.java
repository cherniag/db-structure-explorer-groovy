/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.example.customerservice.server;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import javax.jws.WebResult;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebServiceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.o2.soa.coredata_1.AccountType;
import uk.co.o2.soa.managepostpaytariffdata_2.BusinessPoliciesType;
import uk.co.o2.soa.managepostpaytariffdata_2.GetTariffCompatibleProducts;
import uk.co.o2.soa.managepostpaytariffdata_2.GetTariffCompatibleProductsResponse;
import uk.co.o2.soa.managepostpaytariffdata_2.ServiceContractType;
import uk.co.o2.soa.managepostpaytariffdata_2.ValidTariffsType;
import uk.co.o2.soa.managepostpaytariffservice_2.CancelContractChangeFault;
import uk.co.o2.soa.managepostpaytariffservice_2.ChangeContractFault;
import uk.co.o2.soa.managepostpaytariffservice_2.GetBusinessPoliciesFault;
import uk.co.o2.soa.managepostpaytariffservice_2.GetContractFault;
import uk.co.o2.soa.managepostpaytariffservice_2.GetContractRevisionsFault;
import uk.co.o2.soa.managepostpaytariffservice_2.GetTariffCompatibleProductsFault;
import uk.co.o2.soa.managepostpaytariffservice_2.GetValidTariffsFault;
import uk.co.o2.soa.managepostpaytariffservice_2.ManagePostpayTariffPortType;
import uk.co.o2.soa.pscommonpostpaydata_2.ProductType;

public class ManagePostpayTariffServicePortImpl implements ManagePostpayTariffPortType {
	@Resource WebServiceContext wsContext;

    Logger logger = LoggerFactory.getLogger(ManagePostpayTariffServicePortImpl.class);

	@Override
	@RequestWrapper(localName = "getValidTariffs", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2", className = "uk.co.o2.soa.managepostpaytariffdata_2.GetValidTariffs")
	@WebMethod
	@ResponseWrapper(localName = "getValidTariffsResponse", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2", className = "uk.co.o2.soa.managepostpaytariffdata_2.GetValidTariffsResponse")
	public void getValidTariffs(
			@WebParam(name = "customerId", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") AccountType customerId,
			@WebParam(name = "overrideRules", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") Boolean overrideRules,
			@WebParam(name = "tariffFamily", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") List<String> tariffFamily,
			@WebParam(mode = Mode.OUT, name = "MSISDN", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") Holder<String> msisdn,
			@WebParam(mode = Mode.OUT, name = "validTariffs", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") Holder<ValidTariffsType> validTariffs)
			throws GetValidTariffsFault {
        logger.debug("getValidTariffs CALLED");
    }

	@Override
	@WebResult(name = "currentContract", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2")
	@RequestWrapper(localName = "getContract", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2", className = "uk.co.o2.soa.managepostpaytariffdata_2.GetContract")
	@WebMethod
	@ResponseWrapper(localName = "getContractResponse", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2", className = "uk.co.o2.soa.managepostpaytariffdata_2.GetContractResponse")
	public ServiceContractType getContract(
			@WebParam(name = "customerId", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") AccountType customerId)
			throws GetContractFault {
        logger.debug("getContract CALLED");
		ServiceContractType contract = new ServiceContractType();
        ProductType productType = new ProductType();
        productType.setProductClassification("4G");
        contract.setTariff(productType);
        return contract;
	}

	@Override
	@RequestWrapper(localName = "changeContract", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2", className = "uk.co.o2.soa.managepostpaytariffdata_2.ChangeContract")
	@WebMethod
	@ResponseWrapper(localName = "changeContractResponse", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2", className = "uk.co.o2.soa.managepostpaytariffdata_2.GenericContractResponse")
	public void changeContract(
			@WebParam(name = "customerId", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") AccountType customerId,
			@WebParam(name = "revisedContract", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") ServiceContractType revisedContract,
			@WebParam(name = "additionalProducts", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") List<ProductType> additionalProducts,
			@WebParam(name = "effectiveStartDate", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") XMLGregorianCalendar effectiveStartDate,
			@WebParam(name = "overrideRules", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") Boolean overrideRules,
			@WebParam(mode = Mode.OUT, name = "statusCode", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") Holder<String> statusCode,
			@WebParam(mode = Mode.OUT, name = "statusMessage", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") Holder<String> statusMessage,
			@WebParam(mode = Mode.OUT, name = "serviceRequestId", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") Holder<String> serviceRequestId,
			@WebParam(mode = Mode.OUT, name = "isRulesOverridable", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") Holder<Boolean> isRulesOverridable)
			throws ChangeContractFault {
        logger.debug("changeContract CALLED");
	}

	@Override
	@RequestWrapper(localName = "cancelContractChange", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2", className = "uk.co.o2.soa.managepostpaytariffdata_2.CancelContractChange")
	@WebMethod
	@ResponseWrapper(localName = "cancelContractChangeResponse", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2", className = "uk.co.o2.soa.managepostpaytariffdata_2.GenericContractResponse")
	public void cancelContractChange(
			@WebParam(name = "cancelToken", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") BigInteger cancelToken,
			@WebParam(mode = Mode.OUT, name = "statusCode", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") Holder<String> statusCode,
			@WebParam(mode = Mode.OUT, name = "statusMessage", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") Holder<String> statusMessage,
			@WebParam(mode = Mode.OUT, name = "serviceRequestId", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") Holder<String> serviceRequestId,
			@WebParam(mode = Mode.OUT, name = "isRulesOverridable", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") Holder<Boolean> isRulesOverridable)
			throws CancelContractChangeFault {
        logger.debug("cancelContractChange CALLED");
	}

	@Override
	@RequestWrapper(localName = "getContractRevisions", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2", className = "uk.co.o2.soa.managepostpaytariffdata_2.GetContractRevisions")
	@WebMethod
	@ResponseWrapper(localName = "getContractRevisionsResponse", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2", className = "uk.co.o2.soa.managepostpaytariffdata_2.GetContractRevisionsResponse")
	public void getContractRevisions(
			@WebParam(name = "customerId", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") AccountType customerId,
			@WebParam(mode = Mode.OUT, name = "MSISDN", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") Holder<String> msisdn,
			@WebParam(mode = Mode.OUT, name = "revisedContract", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") Holder<ServiceContractType> revisedContract,
			@WebParam(mode = Mode.OUT, name = "effectiveDate", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") Holder<XMLGregorianCalendar> effectiveDate,
			@WebParam(mode = Mode.OUT, name = "cancelToken", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") Holder<BigInteger> cancelToken)
			throws GetContractRevisionsFault {
        logger.debug("getContractRevisions CALLED");
	}

	@Override
	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	@WebResult(name = "getTariffCompatibleProductsResponse", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2", partName = "getTariffCompatibleProductsResponse")
	@WebMethod
	public GetTariffCompatibleProductsResponse getTariffCompatibleProducts(
			@WebParam(partName = "getTariffCompatibleProducts", name = "getTariffCompatibleProducts", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") GetTariffCompatibleProducts getTariffCompatibleProducts)
			throws GetTariffCompatibleProductsFault {
        logger.debug("getTariffCompatibleProducts CALLED");
		return null;
	}

	@Override
	@WebResult(name = "businessPolicies", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2")
	@RequestWrapper(localName = "getBusinessPolicies", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2", className = "uk.co.o2.soa.managepostpaytariffdata_2.GetBusinessPolicies")
	@WebMethod
	@ResponseWrapper(localName = "getBusinessPoliciesResponse", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2", className = "uk.co.o2.soa.managepostpaytariffdata_2.GetBusinessPoliciesResponse")
	public BusinessPoliciesType getBusinessPolicies(
			@WebParam(name = "customerId", targetNamespace = "http://soa.o2.co.uk/managepostpaytariffdata_2") AccountType customerId)
			throws GetBusinessPoliciesFault {
        logger.debug("getBusinessPolicies CALLED");
		return null;
	}

    
    /*
	@Resource
    WebServiceContext wsContext;
    public List<Customer> getCustomersByName(String name) throws NoSuchCustomerException {
        if ("None".equals(name)) {
            NoSuchCustomer noSuchCustomer = new NoSuchCustomer();
            noSuchCustomer.setCustomerName(name);
            throw new NoSuchCustomerException("Did not find any matching customer for name=" + name,
                                              noSuchCustomer);
        }

        List<Customer> customers = new ArrayList<Customer>();
        for (int c = 0; c < 2; c++) {
            Customer cust = new Customer();
            cust.setName(name);
            cust.getAddress().add("Pine Street 200");
            Date bDate = new GregorianCalendar(2009, 01, 01).getTime();
            cust.setBirthDate(bDate);
            cust.setNumOrders(1);
            cust.setRevenue(10000);
            cust.setTest(new BigDecimal(1.5));
            cust.setType(CustomerType.BUSINESS);
            customers.add(cust);
        }

        return customers;
    }

*/
	
	
}
