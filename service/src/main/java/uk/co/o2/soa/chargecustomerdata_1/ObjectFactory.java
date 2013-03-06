
package uk.co.o2.soa.chargecustomerdata_1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import uk.co.o2.soa.coredata_1.SOAFaultType;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uk.co.o2.soa.chargecustomerdata_1 package. 
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

    private final static QName _BillSubscriberResponse_QNAME = new QName("http://soa.o2.co.uk/chargecustomerdata_1", "billSubscriberResponse");
    private final static QName _BillSubscriber_QNAME = new QName("http://soa.o2.co.uk/chargecustomerdata_1", "billSubscriber");
    private final static QName _BillSubscriberFault_QNAME = new QName("http://soa.o2.co.uk/chargecustomerdata_1", "billSubscriberFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uk.co.o2.soa.chargecustomerdata_1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ServiceResult }
     * 
     */
    public ServiceResult createServiceResult() {
        return new ServiceResult();
    }

    /**
     * Create an instance of {@link MapEntry }
     * 
     */
    public MapEntry createMapEntry() {
        return new MapEntry();
    }

    /**
     * Create an instance of {@link BillSubscriberResponse }
     * 
     */
    public BillSubscriberResponse createBillSubscriberResponse() {
        return new BillSubscriberResponse();
    }

    /**
     * Create an instance of {@link Map }
     * 
     */
    public Map createMap() {
        return new Map();
    }

    /**
     * Create an instance of {@link BillSubscriber }
     * 
     */
    public BillSubscriber createBillSubscriber() {
        return new BillSubscriber();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BillSubscriberResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/chargecustomerdata_1", name = "billSubscriberResponse")
    public JAXBElement<BillSubscriberResponse> createBillSubscriberResponse(BillSubscriberResponse value) {
        return new JAXBElement<BillSubscriberResponse>(_BillSubscriberResponse_QNAME, BillSubscriberResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BillSubscriber }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/chargecustomerdata_1", name = "billSubscriber")
    public JAXBElement<BillSubscriber> createBillSubscriber(BillSubscriber value) {
        return new JAXBElement<BillSubscriber>(_BillSubscriber_QNAME, BillSubscriber.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SOAFaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soa.o2.co.uk/chargecustomerdata_1", name = "billSubscriberFault")
    public JAXBElement<SOAFaultType> createBillSubscriberFault(SOAFaultType value) {
        return new JAXBElement<SOAFaultType>(_BillSubscriberFault_QNAME, SOAFaultType.class, null, value);
    }

}
