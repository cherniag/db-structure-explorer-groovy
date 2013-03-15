
package uk.co.o2.soa.subscriberservice;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import uk.co.o2.soa.coredata.PaymentCategoryType;
import uk.co.o2.soa.coredata.SegmentType;
import uk.co.o2.soa.subscriberdata.BillingProfileType;
import uk.co.o2.soa.subscriberdata.MsisdnList;
import uk.co.o2.soa.subscriberdata.ProductListType;
import uk.co.o2.soa.subscriberdata.ServiceProviderDetailsType;


@WebService(name = "SubscriberPort", targetNamespace = "http://soa.o2.co.uk/subscriberservice_2")
@XmlSeeAlso({
    uk.co.o2.soa.coredata.ObjectFactory.class,
    uk.co.o2.soa.subscriberdata.ObjectFactory.class
})
public interface SubscriberPort {


    @WebMethod
    @WebResult(name = "subscriberProfile", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
    @RequestWrapper(localName = "getSubscriberProfile", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata.GetSubscriberProfile")
    @ResponseWrapper(localName = "getSubscriberProfileResponse", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata.GetSubscriberProfileResponse")
    public uk.co.o2.soa.subscriberdata.SubscriberProfileType getSubscriberProfile(
        @WebParam(name = "subscriberID", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
        String subscriberID)
        throws GetSubscriberProfileFault
    ;

    @WebMethod
    @WebResult(name = "Segment", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
    @RequestWrapper(localName = "getSubscriberSegment", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata.GetSubscriberSegment")
    @ResponseWrapper(localName = "getSubscriberSegmentResponse", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata.GetSubscriberSegmentResponse")
    public SegmentType getSubscriberSegment(
        @WebParam(name = "SubscriberID", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
        String subscriberID)
        throws GetSubscriberSegmentFault
    ;

    @WebMethod
    @WebResult(name = "PaymentCategory", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
    @RequestWrapper(localName = "getSubscriberPaymentCategory", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata.GetSubscriberPaymentCategory")
    @ResponseWrapper(localName = "getSubscriberPaymentCategoryResponse", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata.GetSubscriberPaymentCategoryResponse")
    public PaymentCategoryType getSubscriberPaymentCategory(
        @WebParam(name = "SubscriberID", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
        String subscriberID)
        throws GetSubscriberPaymentCategoryFault
    ;

    @WebMethod
    @WebResult(name = "Operator", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
    @RequestWrapper(localName = "getSubscriberOperator", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata.GetSubscriberOperator")
    @ResponseWrapper(localName = "getSubscriberOperatorResponse", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata.GetSubscriberOperatorResponse")
    public String getSubscriberOperator(
        @WebParam(name = "SubscriberID", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
        String subscriberID)
        throws GetSubscriberOperatorFault
    ;

    @WebMethod
    @WebResult(name = "channel", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
    @RequestWrapper(localName = "getSubscriberChannel", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata.GetSubscriberChannel")
    @ResponseWrapper(localName = "getSubscriberChannelResponse", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata.GetSubscriberChannelResponse")
    public String getSubscriberChannel(
        @WebParam(name = "SubscriberID", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
        String subscriberID)
        throws GetSubscriberChannelFault
    ;

    @WebMethod
    @WebResult(name = "billingProfile", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
    @RequestWrapper(localName = "getBillingSystem", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata.GetBillingSystem")
    @ResponseWrapper(localName = "getBillingSystemResponse", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata.GetBillingSystemResponse")
    public BillingProfileType getBillingSystem(
        @WebParam(name = "msisdn", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
        String msisdn)
        throws GetBillingSystemFault
    ;

    @WebMethod
    @RequestWrapper(localName = "getSubscriberAndBillingSystem", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata.GetSubscriberAndBillingSystem")
    @ResponseWrapper(localName = "getSubscriberAndBillingSystemResponse", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata.GetSubscriberAndBillingSystemResponse")
    public void getSubscriberAndBillingSystem(
        @WebParam(name = "msisdn", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
        String msisdn,
        @WebParam(name = "subscriberProfile", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", mode = WebParam.Mode.OUT)
        Holder<uk.co.o2.soa.coredata.SubscriberProfileType> subscriberProfile,
        @WebParam(name = "billingProfile", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", mode = WebParam.Mode.OUT)
        Holder<BillingProfileType> billingProfile)
        throws GetSubscriberAndBillingSystemFault
    ;

    @WebMethod
    @RequestWrapper(localName = "getBillProfile", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata.GetBillProfile")
    @ResponseWrapper(localName = "getBillProfileResponse", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata.GetBillProfileResponse")
    public void getBillProfile(
        @WebParam(name = "msisdn", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
        String msisdn,
        @WebParam(name = "accountNumber", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", mode = WebParam.Mode.INOUT)
        Holder<String> accountNumber,
        @WebParam(name = "billingSystem", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", mode = WebParam.Mode.OUT)
        Holder<BillingProfileType> billingSystem,
        @WebParam(name = "msisdnList", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", mode = WebParam.Mode.OUT)
        Holder<MsisdnList> msisdnList)
        throws GetBillProfileFault
    ;

    @WebMethod
    @WebResult(name = "chargingCategory", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
    @RequestWrapper(localName = "getChargingCategory", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata.GetChargingCategory")
    @ResponseWrapper(localName = "getChargingCategoryResponse", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata.GetChargingCategoryResponse")
    public List<String> getChargingCategory(
        @WebParam(name = "msisdn", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
        String msisdn,
        @WebParam(name = "productList", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
        ProductListType productList)
        throws GetChargingCategoryFault;

    @WebMethod(operationName = "getSPIDDetails_1")
    @WebResult(name = "serviceProviderDetails", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
    @RequestWrapper(localName = "getSPIDDetails_1", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata.GetSPIDDetails1RequestType")
    @ResponseWrapper(localName = "getSPIDDetails_1Response", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2", className = "uk.co.o2.soa.subscriberdata.GetSPIDDetails1ResponseType")
    public ServiceProviderDetailsType getSPIDDetails1(
        @WebParam(name = "serviceProviderId", targetNamespace = "http://soa.o2.co.uk/subscriberdata_2")
        String serviceProviderId)
        throws GetSPIDDetails1Fault
    ;

}
