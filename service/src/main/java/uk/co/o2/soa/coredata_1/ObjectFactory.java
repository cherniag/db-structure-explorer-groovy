
package uk.co.o2.soa.coredata_1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uk.co.o2.soa.coredata_1 package. 
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

    private final static QName _SubscriberProfile_QNAME = new QName("http://soa.o2.co.uk/coredata_1", "subscriberProfile");
    private final static QName _Operator_QNAME = new QName("http://soa.o2.co.uk/coredata_1", "Operator");
    private final static QName _ApplicationID_QNAME = new QName("http://soa.o2.co.uk/coredata_1", "applicationID");
    private final static QName _DebugFlag_QNAME = new QName("http://soa.o2.co.uk/coredata_1", "debugFlag");
    private final static QName _PaymentCategory_QNAME = new QName("http://soa.o2.co.uk/coredata_1", "PaymentCategory");
    private final static QName _Channel_QNAME = new QName("http://soa.o2.co.uk/coredata_1", "Channel");
    private final static QName _Async_QNAME = new QName("http://soa.o2.co.uk/coredata_1", "async");
    private final static QName _SubscriberID_QNAME = new QName("http://soa.o2.co.uk/coredata_1", "SubscriberID");
    private final static QName _SOAFaultCode_QNAME = new QName("http://soa.o2.co.uk/coredata_1", "SOAFaultCode");
    private final static QName _SOAConsumerTransactionID_QNAME = new QName("http://soa.o2.co.uk/coredata_1", "SOAConsumerTransactionID");
    private final static QName _Segment_QNAME = new QName("http://soa.o2.co.uk/coredata_1", "Segment");
    private final static QName _SOAFault_QNAME = new QName("http://soa.o2.co.uk/coredata_1", "SOAFault");
    private final static QName _SOAFaultOriginator_QNAME = new QName("http://soa.o2.co.uk/coredata_1", "SOAFaultOriginator");
    private final static QName _ProviderID_QNAME = new QName("http://soa.o2.co.uk/coredata_1", "providerID");
    private final static QName _OriginatorIP_QNAME = new QName("http://soa.o2.co.uk/coredata_1", "originatorIP");
    private final static QName _SOATransactionID_QNAME = new QName("http://soa.o2.co.uk/coredata_1", "SOATransactionID");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uk.co.o2.soa.coredata_1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MapItem }
     * 
     */
    public MapItem createMapItem() {
        return new MapItem();
    }

    /**
     * Create an instance of {@link CashCardAccountType }
     * 
     */
    public CashCardAccountType createCashCardAccountType() {
        return new CashCardAccountType();
    }

    /**
     * Create an instance of {@link ServiceResultType }
     * 
     */
    public ServiceResultType createServiceResultType() {
        return new ServiceResultType();
    }

    /**
     * Create an instance of {@link AccountDetail }
     * 
     */
    public AccountDetail createAccountDetail() {
        return new AccountDetail();
    }

    /**
     * Create an instance of {@link AddressMetaDataType }
     * 
     */
    public AddressMetaDataType createAddressMetaDataType() {
        return new AddressMetaDataType();
    }

    /**
     * Create an instance of {@link SubscriberProfileType }
     * 
     */
    public SubscriberProfileType createSubscriberProfileType() {
        return new SubscriberProfileType();
    }

    /**
     * Create an instance of {@link MapType }
     * 
     */
    public MapType createMapType() {
        return new MapType();
    }

    /**
     * Create an instance of {@link SOAFaultType }
     * 
     */
    public SOAFaultType createSOAFaultType() {
        return new SOAFaultType();
    }

    /**
     * Create an instance of {@link AddressType }
     * 
     */
    public AddressType createAddressType() {
        return new AddressType();
    }

    /**
     * Create an instance of {@link AccountType }
     * 
     */
    public AccountType createAccountType() {
        return new AccountType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubscriberProfileType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/coredata_1", name = "subscriberProfile")
    public JAXBElement<SubscriberProfileType> createSubscriberProfile(SubscriberProfileType value) {
        return new JAXBElement<SubscriberProfileType>(_SubscriberProfile_QNAME, SubscriberProfileType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/coredata_1", name = "Operator")
    public JAXBElement<String> createOperator(String value) {
        return new JAXBElement<String>(_Operator_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/coredata_1", name = "applicationID")
    public JAXBElement<String> createApplicationID(String value) {
        return new JAXBElement<String>(_ApplicationID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/coredata_1", name = "debugFlag")
    public JAXBElement<String> createDebugFlag(String value) {
        return new JAXBElement<String>(_DebugFlag_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PaymentCategoryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/coredata_1", name = "PaymentCategory")
    public JAXBElement<PaymentCategoryType> createPaymentCategory(PaymentCategoryType value) {
        return new JAXBElement<PaymentCategoryType>(_PaymentCategory_QNAME, PaymentCategoryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/coredata_1", name = "Channel")
    public JAXBElement<String> createChannel(String value) {
        return new JAXBElement<String>(_Channel_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/coredata_1", name = "async")
    public JAXBElement<Boolean> createAsync(Boolean value) {
        return new JAXBElement<Boolean>(_Async_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/coredata_1", name = "SubscriberID")
    public JAXBElement<String> createSubscriberID(String value) {
        return new JAXBElement<String>(_SubscriberID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/coredata_1", name = "SOAFaultCode")
    public JAXBElement<String> createSOAFaultCode(String value) {
        return new JAXBElement<String>(_SOAFaultCode_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/coredata_1", name = "SOAConsumerTransactionID")
    public JAXBElement<String> createSOAConsumerTransactionID(String value) {
        return new JAXBElement<String>(_SOAConsumerTransactionID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SegmentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/coredata_1", name = "Segment")
    public JAXBElement<SegmentType> createSegment(SegmentType value) {
        return new JAXBElement<SegmentType>(_Segment_QNAME, SegmentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SOAFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/coredata_1", name = "SOAFault")
    public JAXBElement<SOAFaultType> createSOAFault(SOAFaultType value) {
        return new JAXBElement<SOAFaultType>(_SOAFault_QNAME, SOAFaultType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/coredata_1", name = "SOAFaultOriginator")
    public JAXBElement<String> createSOAFaultOriginator(String value) {
        return new JAXBElement<String>(_SOAFaultOriginator_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/coredata_1", name = "providerID")
    public JAXBElement<String> createProviderID(String value) {
        return new JAXBElement<String>(_ProviderID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/coredata_1", name = "originatorIP")
    public JAXBElement<String> createOriginatorIP(String value) {
        return new JAXBElement<String>(_OriginatorIP_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/coredata_1", name = "SOATransactionID")
    public JAXBElement<String> createSOATransactionID(String value) {
        return new JAXBElement<String>(_SOATransactionID_QNAME, String.class, null, value);
    }

}
