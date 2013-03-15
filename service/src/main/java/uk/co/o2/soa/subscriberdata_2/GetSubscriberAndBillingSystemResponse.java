
package uk.co.o2.soa.subscriberdata_2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import uk.co.o2.soa.coredata_1.SubscriberProfileType;


/**
 * Returns the Subscriber Profile and Billing System
 * 
 * <p>Java class for getSubscriberAndBillingSystemResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getSubscriberAndBillingSystemResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="subscriberProfile" type="{http://soa.o2.co.uk/coredata_1}SubscriberProfileType"/>
 *         &lt;element name="billingProfile" type="{http://soa.o2.co.uk/subscriberdata_2}BillingProfileType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getSubscriberAndBillingSystemResponse", propOrder = {
    "subscriberProfile",
    "billingProfile"
})
public class GetSubscriberAndBillingSystemResponse {

    @XmlElement(required = true)
    protected SubscriberProfileType subscriberProfile;
    @XmlElement(required = true)
    protected BillingProfileType billingProfile;

    /**
     * Gets the value of the subscriberProfile property.
     * 
     * @return
     *     possible object is
     *     {@link SubscriberProfileType }
     *     
     */
    public SubscriberProfileType getSubscriberProfile() {
        return subscriberProfile;
    }

    /**
     * Sets the value of the subscriberProfile property.
     * 
     * @param value
     *     allowed object is
     *     {@link SubscriberProfileType }
     *     
     */
    public void setSubscriberProfile(SubscriberProfileType value) {
        this.subscriberProfile = value;
    }

    /**
     * Gets the value of the billingProfile property.
     * 
     * @return
     *     possible object is
     *     {@link BillingProfileType }
     *     
     */
    public BillingProfileType getBillingProfile() {
        return billingProfile;
    }

    /**
     * Sets the value of the billingProfile property.
     * 
     * @param value
     *     allowed object is
     *     {@link BillingProfileType }
     *     
     */
    public void setBillingProfile(BillingProfileType value) {
        this.billingProfile = value;
    }

}
