
package uk.co.o2.soa.subscriberdata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The returned profile
 * 
 * <p>Java class for getSubscriberProfileResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getSubscriberProfileResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="subscriberProfile" type="{http://soa.o2.co.uk/subscriberdata_2}subscriberProfileType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getSubscriberProfileResponse", propOrder = {
    "subscriberProfile"
})
public class GetSubscriberProfileResponse {

    @XmlElement(required = true)
    protected SubscriberProfileType subscriberProfile;

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

}
