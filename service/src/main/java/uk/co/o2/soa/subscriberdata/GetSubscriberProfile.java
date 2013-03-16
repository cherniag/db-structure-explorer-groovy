
package uk.co.o2.soa.subscriberdata;

import javax.xml.bind.annotation.*;


/**
 * Request to get profile for MSISDN
 * 
 * <p>Java class for getSubscriberProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getSubscriberProfile">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="subscriberID" type="{http://soa.o2.co.uk/coredata_1}msisdnType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "getSubscriberProfile", namespace="http://soa.o2.co.uk/subscriberdata_2")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getSubscriberProfile", propOrder = {
    "subscriberID"
})
public class GetSubscriberProfile {

    @XmlElement(required = true)
    protected String subscriberID;

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

}
