package com.musicqubed.o2services;

import java.util.List;

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

import uk.co.o2.soa.coredata_1.ServiceResultType;
import uk.co.o2.soa.manageorderdata_2.AccountDirectDebitDetailsType;
import uk.co.o2.soa.manageorderdata_2.AccountSetType;
import uk.co.o2.soa.manageorderdata_2.CCADetailsType;
import uk.co.o2.soa.manageorderdata_2.ConsignmentDetailsType;
import uk.co.o2.soa.manageorderdata_2.GetOrderDetails;
import uk.co.o2.soa.manageorderdata_2.GetOrderDetails2;
import uk.co.o2.soa.manageorderdata_2.GetOrderDetails2Response;
import uk.co.o2.soa.manageorderdata_2.GetOrderDetailsResponse;
import uk.co.o2.soa.manageorderdata_2.GetOrderList;
import uk.co.o2.soa.manageorderdata_2.GetOrderList2;
import uk.co.o2.soa.manageorderdata_2.GetOrderList2Response;
import uk.co.o2.soa.manageorderdata_2.GetOrderListResponse;
import uk.co.o2.soa.manageorderdata_2.InitialPaymentDetailsType;
import uk.co.o2.soa.manageorderdata_2.ListOfDeliveryOptionsType;
import uk.co.o2.soa.manageorderdata_2.OrderCreditCheckDetailsType;
import uk.co.o2.soa.manageorderdata_2.OrderDeliveryDetailsType;
import uk.co.o2.soa.manageorderdata_2.OrderHeaderType;
import uk.co.o2.soa.manageorderdata_2.OrderSourceDetailsType;
import uk.co.o2.soa.manageorderdata_2.PartySetType;
import uk.co.o2.soa.manageorderdata_2.ResponseHeaderType;
import uk.co.o2.soa.manageorderdata_2.ReturnedLeasedDeviceDetailsType;
import uk.co.o2.soa.manageorderdata_2.RuleOutcomeType;
import uk.co.o2.soa.manageorderdata_2.SaleableProductsType;
import uk.co.o2.soa.manageorderdata_2.SetConsignmentStateInOrder1;
import uk.co.o2.soa.manageorderdata_2.SetConsignmentStateInOrder1Response;
import uk.co.o2.soa.manageorderdata_2.TotalOrderPriceDetailsType;
import uk.co.o2.soa.manageorderdata_2.UpgradeCreditCheckDetailsType;
import uk.co.o2.soa.manageorderdata_2.UpgradeDeliveryDetailsType;
import uk.co.o2.soa.manageorderdata_2.UpgradeDiscountsType;
import uk.co.o2.soa.manageorderdata_2.UpgradeEntitlementDetailsType;
import uk.co.o2.soa.manageorderdata_2.UpgradeInitialPaymentDetailsType;
import uk.co.o2.soa.manageorderdata_2.UpgradeOrderTotalDetailsType;
import uk.co.o2.soa.manageorderdata_2.UpgradeSaleableProductsType;
import uk.co.o2.soa.manageorderdata_2.UpgradeSourceDetailsType;
import uk.co.o2.soa.manageorderservice_2.GetConsignmentDetails1Fault;
import uk.co.o2.soa.manageorderservice_2.GetDeliveryOptions1Fault;
import uk.co.o2.soa.manageorderservice_2.GetOrderDetails2Fault;
import uk.co.o2.soa.manageorderservice_2.GetOrderDetailsFault;
import uk.co.o2.soa.manageorderservice_2.GetOrderList2Fault;
import uk.co.o2.soa.manageorderservice_2.GetOrderListFault;
import uk.co.o2.soa.manageorderservice_2.ManageOrderPortType;
import uk.co.o2.soa.manageorderservice_2.SetConsignmentStateInOrder1Fault;
import uk.co.o2.soa.manageorderservice_2.SubmitOrderFault;
import uk.co.o2.soa.manageorderservice_2.SubmitUpgradeOrder1Fault;
import uk.co.o2.soa.pscommonpostpaydata_2.OrderType;
import uk.co.o2.soa.pscommonpostpaydata_2.OutcomeType;
import uk.co.o2.soa.pscommonpostpaydata_2.TypeYesOrNo;

public class ManageOrderPortImpl implements ManageOrderPortType{

	@Override
	@RequestWrapper(localName = "submitUpgradeOrder_1", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2", className = "uk.co.o2.soa.manageorderdata_2.SubmitUpgradeOrder1")
	@WebMethod(operationName = "submitUpgradeOrder_1")
	@ResponseWrapper(localName = "submitUpgradeOrder_1Response", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2", className = "uk.co.o2.soa.manageorderdata_2.SubmitUpgradeOrder1Response")
	public void submitUpgradeOrder1(
			@WebParam(mode = Mode.INOUT, name = "orderNumber", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") Holder<String> orderNumber,
			@WebParam(mode = Mode.INOUT, name = "upgradeMsisdn", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") Holder<String> upgradeMsisdn,
			@WebParam(name = "customerNumber", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") String customerNumber,
			@WebParam(name = "orderTimeStamp", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") XMLGregorianCalendar orderTimeStamp,
			@WebParam(name = "sourceDetails", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") UpgradeSourceDetailsType sourceDetails,
			@WebParam(name = "initialPaymentDetails", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") UpgradeInitialPaymentDetailsType initialPaymentDetails,
			@WebParam(name = "orderTotalDetails", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") UpgradeOrderTotalDetailsType orderTotalDetails,
			@WebParam(name = "upgradeDiscounts", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") UpgradeDiscountsType upgradeDiscounts,
			@WebParam(name = "CCADetails", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") CCADetailsType ccaDetails,
			@WebParam(name = "upgradeEntitlementDetails", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") UpgradeEntitlementDetailsType upgradeEntitlementDetails,
			@WebParam(name = "saleableProducts", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") List<UpgradeSaleableProductsType> saleableProducts,
			@WebParam(name = "deliveryDetails", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") UpgradeDeliveryDetailsType deliveryDetails,
			@WebParam(name = "creditCheckDetails", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") UpgradeCreditCheckDetailsType creditCheckDetails,
			@WebParam(name = "returnedLeaseDeviceDetails", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") ReturnedLeasedDeviceDetailsType returnedLeaseDeviceDetails,
			@WebParam(name = "updateAccountRecurringPaymentDetails", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") AccountDirectDebitDetailsType updateAccountRecurringPaymentDetails,
			@WebParam(mode = Mode.OUT, name = "status", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") Holder<ServiceResultType> status)
			throws SubmitUpgradeOrder1Fault {
		// TODO Auto-generated method stub
		
	}

	@Override
	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	@WebResult(name = "getOrderList_2Response", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2", partName = "getOrderList_2Response")
	@WebMethod(operationName = "getOrderList_2")
	public GetOrderList2Response getOrderList2(
			@WebParam(partName = "getOrderList_2", name = "getOrderList_2", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") GetOrderList2 getOrderList2)
			throws GetOrderList2Fault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	@WebResult(name = "getOrderDetails_2Response", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2", partName = "getOrderDetails_2Response")
	@WebMethod(operationName = "getOrderDetails_2")
	public GetOrderDetails2Response getOrderDetails2(
			@WebParam(partName = "getOrderDetails_2", name = "getOrderDetails_2", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") GetOrderDetails2 getOrderDetails2)
			throws GetOrderDetails2Fault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@RequestWrapper(localName = "submitOrder", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2", className = "uk.co.o2.soa.manageorderdata_2.SubmitOrder")
	@WebMethod
	@ResponseWrapper(localName = "submitOrderResponse", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2", className = "uk.co.o2.soa.manageorderdata_2.SubmitOrderResponse")
	public void submitOrder(
			@WebParam(name = "orderHeader", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") OrderHeaderType orderHeader,
			@WebParam(name = "orderSourceDetails", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") OrderSourceDetailsType orderSourceDetails,
			@WebParam(name = "initialPaymentDetails", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") InitialPaymentDetailsType initialPaymentDetails,
			@WebParam(name = "CCADetails", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") List<CCADetailsType> ccaDetails,
			@WebParam(name = "totalOrderPriceDetails", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") TotalOrderPriceDetailsType totalOrderPriceDetails,
			@WebParam(name = "saleableProducts", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") List<SaleableProductsType> saleableProducts,
			@WebParam(name = "accountSet", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") List<AccountSetType> accountSet,
			@WebParam(name = "partySet", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") List<PartySetType> partySet,
			@WebParam(name = "orderDeliveryDetails", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") OrderDeliveryDetailsType orderDeliveryDetails,
			@WebParam(name = "orderCreditCheckDetails", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") OrderCreditCheckDetailsType orderCreditCheckDetails,
			@WebParam(mode = Mode.OUT, name = "responseHeader", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") Holder<ResponseHeaderType> responseHeader,
			@WebParam(mode = Mode.OUT, name = "acceptStatus", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") Holder<OutcomeType> acceptStatus,
			@WebParam(mode = Mode.OUT, name = "ruleOutcome", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") Holder<RuleOutcomeType> ruleOutcome)
			throws SubmitOrderFault {
		// TODO Auto-generated method stub
		
	}

	@Override
	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	@WebResult(name = "getOrderListResponse", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2", partName = "getOrderListResponse")
	@WebMethod
	public GetOrderListResponse getOrderList(
			@WebParam(partName = "getOrderList", name = "getOrderList", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") GetOrderList getOrderList)
			throws GetOrderListFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	@WebResult(name = "setConsignmentStateInOrder_1Response", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2", partName = "setConsignmentStateInOrder_1Response")
	@WebMethod(operationName = "setConsignmentStateInOrder_1")
	public SetConsignmentStateInOrder1Response setConsignmentStateInOrder1(
			@WebParam(partName = "setConsignmentStateInOrder_1", name = "setConsignmentStateInOrder_1", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") SetConsignmentStateInOrder1 setConsignmentStateInOrder1)
			throws SetConsignmentStateInOrder1Fault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@WebResult(name = "orderConsignmentDetails", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2")
	@RequestWrapper(localName = "getConsignmentDetails_1", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2", className = "uk.co.o2.soa.manageorderdata_2.GetConsignmentDetails1")
	@WebMethod(operationName = "getConsignmentDetails_1")
	@ResponseWrapper(localName = "getConsignmentDetails_1Response", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2", className = "uk.co.o2.soa.manageorderdata_2.GetConsignmentDetails1Response")
	public List<ConsignmentDetailsType> getConsignmentDetails1(
			@WebParam(name = "orderNumber", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") String orderNumber,
			@WebParam(name = "consignmentNumber", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") String consignmentNumber,
			@WebParam(name = "storeNumber", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") String storeNumber)
			throws GetConsignmentDetails1Fault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@SOAPBinding(parameterStyle = ParameterStyle.BARE)
	@WebResult(name = "getOrderDetailsResponse", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2", partName = "getOrderDetailsResponse")
	@WebMethod
	public GetOrderDetailsResponse getOrderDetails(
			@WebParam(partName = "getOrderDetails", name = "getOrderDetails", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") GetOrderDetails getOrderDetails)
			throws GetOrderDetailsFault {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@WebResult(name = "listOfDeliveryOptions", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2")
	@RequestWrapper(localName = "getDeliveryOptions_1", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2", className = "uk.co.o2.soa.manageorderdata_2.GetDeliveryOptions1")
	@WebMethod(operationName = "getDeliveryOptions_1")
	@ResponseWrapper(localName = "getDeliveryOptions_1Response", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2", className = "uk.co.o2.soa.manageorderdata_2.GetDeliveryOptions1Response")
	public ListOfDeliveryOptionsType getDeliveryOptions1(
			@WebParam(name = "orderType", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") OrderType orderType,
			@WebParam(name = "orderDate", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") XMLGregorianCalendar orderDate,
			@WebParam(name = "postCode", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") String postCode,
			@WebParam(name = "isActiveReplacement", targetNamespace = "http://soa.o2.co.uk/manageorderdata_2") TypeYesOrNo isActiveReplacement)
			throws GetDeliveryOptions1Fault {
		// TODO Auto-generated method stub
		return null;
	}
	

}
