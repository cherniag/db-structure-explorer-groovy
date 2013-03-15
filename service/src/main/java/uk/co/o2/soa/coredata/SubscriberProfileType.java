
package uk.co.o2.soa.coredata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Complex Type that defines Subscriber Profile
 * 			
 * 
 * <p>Java class for SubscriberProfileType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SubscriberProfileType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://soa.o2.co.uk/coredata_1}SubscriberID"/>
 *         &lt;element ref="{http://soa.o2.co.uk/coredata_1}Operator" minOccurs="0"/>
 *         &lt;element ref="{http://soa.o2.co.uk/coredata_1}PaymentCategory" minOccurs="0"/>
 *         &lt;element ref="{http://soa.o2.co.uk/coredata_1}Segment" minOccurs="0"/>
 *         &lt;element name="channel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ServiceProviderID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubscriberProfileType", propOrder = {
    "subscriberID",
    "operator",
    "paymentCategory",
    "segment",
    "channel",
    "serviceProviderID",
    "status"
})
public class SubscriberProfileType {

    @XmlElement(name = "SubscriberID", required = true)
    protected String subscriberID;
    @XmlElement(name = "Operator")
    protected String operator;
    @XmlElement(name = "PaymentCategory")
    protected PaymentCategoryType paymentCategory;
    @XmlElement(name = "Segment")
    protected SegmentType segment;
    protected String channel;
    @XmlElement(name = "ServiceProviderID")
    protected Long serviceProviderID;
    @XmlElement(name = "Status", required = true)
    protected String status;

    /**
     * 
     * 						This refers to the MSISDN for Mobile subscribers
     * 					
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubscriberID() {
        return subscriberID;
    }

    /**
     * Sets the value of the subscriberID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubscriberID(String value) {
        this.subscriberID = value;
    }

    /**
     * 
     * 						Operator ID for the particular subscriber
     * 					
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Sets the value of the operator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperator(String value) {
        this.operator = value;
    }

    /**
     * 
     * 						Type of payment, Prepay of Postpay
     * 					
     * 
     * @return
     *     possible object is
     *     {@link PaymentCategoryType }
     *     
     */
    public PaymentCategoryType getPaymentCategory() {
        return paymentCategory;
    }

    /**
     * Sets the value of the paymentCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentCategoryType }
     *     
     */
    public void setPaymentCategory(PaymentCategoryType value) {
        this.paymentCategory = value;
    }

    /**
     * 
     * 						Segment of Subscriber, corporate, consumer, ISP
     * 						etc.
     * 					
     * 
     * @return
     *     possible object is
     *     {@link SegmentType }
     *     
     */
    public SegmentType getSegment() {
        return segment;
    }

    /**
     * Sets the value of the segment property.
     * 
     * @param value
     *     allowed object is
     *     {@link SegmentType }
     *     
     */
    public void setSegment(SegmentType value) {
        this.segment = value;
    }

    /**
     * Gets the value of the channel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Sets the value of the channel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChannel(String value) {
        this.channel = value;
    }

    /**
     * Gets the value of the serviceProviderID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getServiceProviderID() {
        return serviceProviderID;
    }

    /**
     * Sets the value of the serviceProviderID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setServiceProviderID(Long value) {
        this.serviceProviderID = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

}
