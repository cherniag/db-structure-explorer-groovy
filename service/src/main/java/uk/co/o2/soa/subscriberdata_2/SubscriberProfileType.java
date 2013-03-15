
package uk.co.o2.soa.subscriberdata_2;

import javax.xml.bind.annotation.*;

import uk.co.o2.soa.coredata.PaymentCategoryType;
import uk.co.o2.soa.coredata.SegmentType;


/**
 * Complex Type that defines Subscriber Profile
 * 
 * <p>Java class for subscriberProfileType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="subscriberProfileType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="subscriberID" type="{http://soa.o2.co.uk/coredata_1}msisdnType"/>
 *         &lt;element name="operator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paymentCategory" type="{http://soa.o2.co.uk/coredata_1}PaymentCategoryType" minOccurs="0"/>
 *         &lt;element name="segment" type="{http://soa.o2.co.uk/coredata_1}SegmentType" minOccurs="0"/>
 *         &lt;element name="channel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serviceProviderID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="puk" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subscriberProfileType", propOrder = {
    "subscriberID",
    "operator",
    "paymentCategory",
    "segment",
    "channel",
    "serviceProviderID",
    "status",
    "puk"
})
public class SubscriberProfileType {

    @XmlElement(required = true)
    protected String subscriberID;
    protected String operator;
    protected PaymentCategoryType paymentCategory;
    protected SegmentType segment;
    protected String channel;
    protected Long serviceProviderID;
    @XmlElement(required = true)
    protected String status;
    protected String puk;

    /**
     * Gets the value of the subscriberID property.
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
     * Gets the value of the operator property.
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
     * Gets the value of the paymentCategory property.
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
     * Gets the value of the segment property.
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

    /**
     * Gets the value of the puk property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPuk() {
        return puk;
    }

    /**
     * Sets the value of the puk property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPuk(String value) {
        this.puk = value;
    }

}
