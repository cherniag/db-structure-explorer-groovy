
package uk.co.o2.soa.subscriberdata;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import uk.co.o2.soa.coredata.SOAFaultType;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uk.co.o2.soa.subscriberdata. package.
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetSubscriberOperatorFault_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getSubscriberOperatorFault");
    private final static QName _GetSPIDDetails1_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getSPIDDetails_1");
    private final static QName _GetChargingCategory_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getChargingCategory");
    private final static QName _GetSubscriberPaymentCategoryFault_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getSubscriberPaymentCategoryFault");
    private final static QName _GetSubscriberSegmentFault_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getSubscriberSegmentFault");
    private final static QName _GetSPIDDetails1Response_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getSPIDDetails_1Response");
    private final static QName _GetSubscriberProfileFault_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getSubscriberProfileFault");
    private final static QName _GetBillingSystemFault_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getBillingSystemFault");
    private final static QName _GetBillProfileFault_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getBillProfileFault");
    private final static QName _GetBillingSystemResponse_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getBillingSystemResponse");
    private final static QName _GetSubscriberChannel_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getSubscriberChannel");
    private final static QName _GetChargingCategoryResponse_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getChargingCategoryResponse");
    private final static QName _GetChargingCategoryFault_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getChargingCategoryFault");
    private final static QName _GetSubscriberOperatorResponse_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getSubscriberOperatorResponse");
    private final static QName _GetSubscriberAndBillingSystemFault_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getSubscriberAndBillingSystemFault");
    private final static QName _GetSubscriberOperator_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getSubscriberOperator");
    private final static QName _GetSPIDDetails1Fault_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getSPIDDetails_1Fault");
    private final static QName _GetSubscriberAndBillingSystemResponse_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getSubscriberAndBillingSystemResponse");
    private final static QName _GetBillProfileResponse_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getBillProfileResponse");
    private final static QName _GetSubscriberPaymentCategory_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getSubscriberPaymentCategory");
    private final static QName _GetSubscriberProfileResponse_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getSubscriberProfileResponse");
    private final static QName _GetSubscriberChannelResponse_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getSubscriberChannelResponse");
    private final static QName _GetSubscriberSegmentResponse_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getSubscriberSegmentResponse");
    private final static QName _GetBillProfile_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getBillProfile");
    private final static QName _GetSubscriberSegment_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getSubscriberSegment");
    private final static QName _GetSubscriberChannelFault_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getSubscriberChannelFault");
    private final static QName _GetSubscriberProfile_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getSubscriberProfile");
    private final static QName _GetSubscriberPaymentCategoryResponse_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getSubscriberPaymentCategoryResponse");
    private final static QName _GetSubscriberAndBillingSystem_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getSubscriberAndBillingSystem");
    private final static QName _GetBillingSystem_QNAME = new QName("http://soa.o2.co.uk/subscriberdata_2", "getBillingSystem");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uk.co.o2.soa.subscriberdata.
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetSubscriberAndBillingSystemResponse }
     * 
     */
    public GetSubscriberAndBillingSystemResponse createGetSubscriberAndBillingSystemResponse() {
        return new GetSubscriberAndBillingSystemResponse();
    }

    /**
     * Create an instance of {@link GetSubscriberSegmentResponse }
     * 
     */
    public GetSubscriberSegmentResponse createGetSubscriberSegmentResponse() {
        return new GetSubscriberSegmentResponse();
    }

    /**
     * Create an instance of {@link GetSubscriberAndBillingSystem }
     * 
     */
    public GetSubscriberAndBillingSystem createGetSubscriberAndBillingSystem() {
        return new GetSubscriberAndBillingSystem();
    }

    /**
     * Create an instance of {@link ProductListType }
     * 
     */
    public ProductListType createProductListType() {
        return new ProductListType();
    }

    /**
     * Create an instance of {@link GetBillProfile }
     * 
     */
    public GetBillProfile createGetBillProfile() {
        return new GetBillProfile();
    }

    /**
     * Create an instance of {@link GetSubscriberProfile }
     * 
     */
    public GetSubscriberProfile createGetSubscriberProfile() {
        return new GetSubscriberProfile();
    }

    /**
     * Create an instance of {@link SubscriberProfileType }
     * 
     */
    public SubscriberProfileType createSubscriberProfileType() {
        return new SubscriberProfileType();
    }

    /**
     * Create an instance of {@link GetSubscriberProfileResponse }
     * 
     */
    public GetSubscriberProfileResponse createGetSubscriberProfileResponse() {
        return new GetSubscriberProfileResponse();
    }

    /**
     * Create an instance of {@link GetChargingCategory }
     * 
     */
    public GetChargingCategory createGetChargingCategory() {
        return new GetChargingCategory();
    }

    /**
     * Create an instance of {@link GetBillingSystem }
     * 
     */
    public GetBillingSystem createGetBillingSystem() {
        return new GetBillingSystem();
    }

    /**
     * Create an instance of {@link GetSubscriberOperator }
     * 
     */
    public GetSubscriberOperator createGetSubscriberOperator() {
        return new GetSubscriberOperator();
    }

    /**
     * Create an instance of {@link GetBillProfileResponse }
     * 
     */
    public GetBillProfileResponse createGetBillProfileResponse() {
        return new GetBillProfileResponse();
    }

    /**
     * Create an instance of {@link GetSubscriberChannelResponse }
     * 
     */
    public GetSubscriberChannelResponse createGetSubscriberChannelResponse() {
        return new GetSubscriberChannelResponse();
    }

    /**
     * Create an instance of {@link GetSubscriberChannel }
     * 
     */
    public GetSubscriberChannel createGetSubscriberChannel() {
        return new GetSubscriberChannel();
    }

    /**
     * Create an instance of {@link GetSubscriberOperatorResponse }
     * 
     */
    public GetSubscriberOperatorResponse createGetSubscriberOperatorResponse() {
        return new GetSubscriberOperatorResponse();
    }

    /**
     * Create an instance of {@link ServiceProviderDetailsType }
     * 
     */
    public ServiceProviderDetailsType createServiceProviderDetailsType() {
        return new ServiceProviderDetailsType();
    }

    /**
     * Create an instance of {@link GetSubscriberPaymentCategory }
     * 
     */
    public GetSubscriberPaymentCategory createGetSubscriberPaymentCategory() {
        return new GetSubscriberPaymentCategory();
    }

    /**
     * Create an instance of {@link ServicesType }
     * 
     */
    public ServicesType createServicesType() {
        return new ServicesType();
    }

    /**
     * Create an instance of {@link GetSubscriberPaymentCategoryResponse }
     * 
     */
    public GetSubscriberPaymentCategoryResponse createGetSubscriberPaymentCategoryResponse() {
        return new GetSubscriberPaymentCategoryResponse();
    }

    /**
     * Create an instance of {@link GetSPIDDetails1RequestType }
     * 
     */
    public GetSPIDDetails1RequestType createGetSPIDDetails1RequestType() {
        return new GetSPIDDetails1RequestType();
    }

    /**
     * Create an instance of {@link MsisdnList }
     * 
     */
    public MsisdnList createMsisdnList() {
        return new MsisdnList();
    }

    /**
     * Create an instance of {@link GetChargingCategoryResponse }
     * 
     */
    public GetChargingCategoryResponse createGetChargingCategoryResponse() {
        return new GetChargingCategoryResponse();
    }

    /**
     * Create an instance of {@link GetSubscriberSegment }
     * 
     */
    public GetSubscriberSegment createGetSubscriberSegment() {
        return new GetSubscriberSegment();
    }

    /**
     * Create an instance of {@link GetSPIDDetails1ResponseType }
     * 
     */
    public GetSPIDDetails1ResponseType createGetSPIDDetails1ResponseType() {
        return new GetSPIDDetails1ResponseType();
    }

    /**
     * Create an instance of {@link GetBillingSystemResponse }
     * 
     */
    public GetBillingSystemResponse createGetBillingSystemResponse() {
        return new GetBillingSystemResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SOAFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getSubscriberOperatorFault")
    public JAXBElement<SOAFaultType> createGetSubscriberOperatorFault(SOAFaultType value) {
        return new JAXBElement<SOAFaultType>(_GetSubscriberOperatorFault_QNAME, SOAFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSPIDDetails1RequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getSPIDDetails_1")
    public JAXBElement<GetSPIDDetails1RequestType> createGetSPIDDetails1(GetSPIDDetails1RequestType value) {
        return new JAXBElement<GetSPIDDetails1RequestType>(_GetSPIDDetails1_QNAME, GetSPIDDetails1RequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetChargingCategory }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getChargingCategory")
    public JAXBElement<GetChargingCategory> createGetChargingCategory(GetChargingCategory value) {
        return new JAXBElement<GetChargingCategory>(_GetChargingCategory_QNAME, GetChargingCategory.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SOAFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getSubscriberPaymentCategoryFault")
    public JAXBElement<SOAFaultType> createGetSubscriberPaymentCategoryFault(SOAFaultType value) {
        return new JAXBElement<SOAFaultType>(_GetSubscriberPaymentCategoryFault_QNAME, SOAFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SOAFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getSubscriberSegmentFault")
    public JAXBElement<SOAFaultType> createGetSubscriberSegmentFault(SOAFaultType value) {
        return new JAXBElement<SOAFaultType>(_GetSubscriberSegmentFault_QNAME, SOAFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSPIDDetails1ResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getSPIDDetails_1Response")
    public JAXBElement<GetSPIDDetails1ResponseType> createGetSPIDDetails1Response(GetSPIDDetails1ResponseType value) {
        return new JAXBElement<GetSPIDDetails1ResponseType>(_GetSPIDDetails1Response_QNAME, GetSPIDDetails1ResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SOAFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getSubscriberProfileFault")
    public JAXBElement<SOAFaultType> createGetSubscriberProfileFault(SOAFaultType value) {
        return new JAXBElement<SOAFaultType>(_GetSubscriberProfileFault_QNAME, SOAFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SOAFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getBillingSystemFault")
    public JAXBElement<SOAFaultType> createGetBillingSystemFault(SOAFaultType value) {
        return new JAXBElement<SOAFaultType>(_GetBillingSystemFault_QNAME, SOAFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SOAFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getBillProfileFault")
    public JAXBElement<SOAFaultType> createGetBillProfileFault(SOAFaultType value) {
        return new JAXBElement<SOAFaultType>(_GetBillProfileFault_QNAME, SOAFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetBillingSystemResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getBillingSystemResponse")
    public JAXBElement<GetBillingSystemResponse> createGetBillingSystemResponse(GetBillingSystemResponse value) {
        return new JAXBElement<GetBillingSystemResponse>(_GetBillingSystemResponse_QNAME, GetBillingSystemResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSubscriberChannel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getSubscriberChannel")
    public JAXBElement<GetSubscriberChannel> createGetSubscriberChannel(GetSubscriberChannel value) {
        return new JAXBElement<GetSubscriberChannel>(_GetSubscriberChannel_QNAME, GetSubscriberChannel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetChargingCategoryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getChargingCategoryResponse")
    public JAXBElement<GetChargingCategoryResponse> createGetChargingCategoryResponse(GetChargingCategoryResponse value) {
        return new JAXBElement<GetChargingCategoryResponse>(_GetChargingCategoryResponse_QNAME, GetChargingCategoryResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SOAFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getChargingCategoryFault")
    public JAXBElement<SOAFaultType> createGetChargingCategoryFault(SOAFaultType value) {
        return new JAXBElement<SOAFaultType>(_GetChargingCategoryFault_QNAME, SOAFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSubscriberOperatorResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getSubscriberOperatorResponse")
    public JAXBElement<GetSubscriberOperatorResponse> createGetSubscriberOperatorResponse(GetSubscriberOperatorResponse value) {
        return new JAXBElement<GetSubscriberOperatorResponse>(_GetSubscriberOperatorResponse_QNAME, GetSubscriberOperatorResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SOAFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getSubscriberAndBillingSystemFault")
    public JAXBElement<SOAFaultType> createGetSubscriberAndBillingSystemFault(SOAFaultType value) {
        return new JAXBElement<SOAFaultType>(_GetSubscriberAndBillingSystemFault_QNAME, SOAFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSubscriberOperator }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getSubscriberOperator")
    public JAXBElement<GetSubscriberOperator> createGetSubscriberOperator(GetSubscriberOperator value) {
        return new JAXBElement<GetSubscriberOperator>(_GetSubscriberOperator_QNAME, GetSubscriberOperator.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SOAFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getSPIDDetails_1Fault")
    public JAXBElement<SOAFaultType> createGetSPIDDetails1Fault(SOAFaultType value) {
        return new JAXBElement<SOAFaultType>(_GetSPIDDetails1Fault_QNAME, SOAFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSubscriberAndBillingSystemResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getSubscriberAndBillingSystemResponse")
    public JAXBElement<GetSubscriberAndBillingSystemResponse> createGetSubscriberAndBillingSystemResponse(GetSubscriberAndBillingSystemResponse value) {
        return new JAXBElement<GetSubscriberAndBillingSystemResponse>(_GetSubscriberAndBillingSystemResponse_QNAME, GetSubscriberAndBillingSystemResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetBillProfileResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getBillProfileResponse")
    public JAXBElement<GetBillProfileResponse> createGetBillProfileResponse(GetBillProfileResponse value) {
        return new JAXBElement<GetBillProfileResponse>(_GetBillProfileResponse_QNAME, GetBillProfileResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSubscriberPaymentCategory }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getSubscriberPaymentCategory")
    public JAXBElement<GetSubscriberPaymentCategory> createGetSubscriberPaymentCategory(GetSubscriberPaymentCategory value) {
        return new JAXBElement<GetSubscriberPaymentCategory>(_GetSubscriberPaymentCategory_QNAME, GetSubscriberPaymentCategory.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSubscriberProfileResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getSubscriberProfileResponse")
    public JAXBElement<GetSubscriberProfileResponse> createGetSubscriberProfileResponse(GetSubscriberProfileResponse value) {
        return new JAXBElement<GetSubscriberProfileResponse>(_GetSubscriberProfileResponse_QNAME, GetSubscriberProfileResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSubscriberChannelResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getSubscriberChannelResponse")
    public JAXBElement<GetSubscriberChannelResponse> createGetSubscriberChannelResponse(GetSubscriberChannelResponse value) {
        return new JAXBElement<GetSubscriberChannelResponse>(_GetSubscriberChannelResponse_QNAME, GetSubscriberChannelResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSubscriberSegmentResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getSubscriberSegmentResponse")
    public JAXBElement<GetSubscriberSegmentResponse> createGetSubscriberSegmentResponse(GetSubscriberSegmentResponse value) {
        return new JAXBElement<GetSubscriberSegmentResponse>(_GetSubscriberSegmentResponse_QNAME, GetSubscriberSegmentResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetBillProfile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getBillProfile")
    public JAXBElement<GetBillProfile> createGetBillProfile(GetBillProfile value) {
        return new JAXBElement<GetBillProfile>(_GetBillProfile_QNAME, GetBillProfile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSubscriberSegment }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getSubscriberSegment")
    public JAXBElement<GetSubscriberSegment> createGetSubscriberSegment(GetSubscriberSegment value) {
        return new JAXBElement<GetSubscriberSegment>(_GetSubscriberSegment_QNAME, GetSubscriberSegment.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SOAFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getSubscriberChannelFault")
    public JAXBElement<SOAFaultType> createGetSubscriberChannelFault(SOAFaultType value) {
        return new JAXBElement<SOAFaultType>(_GetSubscriberChannelFault_QNAME, SOAFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSubscriberProfile }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getSubscriberProfile")
    public JAXBElement<GetSubscriberProfile> createGetSubscriberProfile(GetSubscriberProfile value) {
        return new JAXBElement<GetSubscriberProfile>(_GetSubscriberProfile_QNAME, GetSubscriberProfile.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSubscriberPaymentCategoryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getSubscriberPaymentCategoryResponse")
    public JAXBElement<GetSubscriberPaymentCategoryResponse> createGetSubscriberPaymentCategoryResponse(GetSubscriberPaymentCategoryResponse value) {
        return new JAXBElement<GetSubscriberPaymentCategoryResponse>(_GetSubscriberPaymentCategoryResponse_QNAME, GetSubscriberPaymentCategoryResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSubscriberAndBillingSystem }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getSubscriberAndBillingSystem")
    public JAXBElement<GetSubscriberAndBillingSystem> createGetSubscriberAndBillingSystem(GetSubscriberAndBillingSystem value) {
        return new JAXBElement<GetSubscriberAndBillingSystem>(_GetSubscriberAndBillingSystem_QNAME, GetSubscriberAndBillingSystem.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetBillingSystem }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/subscriberdata_2", name = "getBillingSystem")
    public JAXBElement<GetBillingSystem> createGetBillingSystem(GetBillingSystem value) {
        return new JAXBElement<GetBillingSystem>(_GetBillingSystem_QNAME, GetBillingSystem.class, null, value);
    }

}
