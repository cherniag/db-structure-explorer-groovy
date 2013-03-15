
package uk.co.o2.soa.subscriberdata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Returns the Billing System
 * 
 * <p>Java class for getBillingSystemResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getBillingSystemResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
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
@XmlType(name = "getBillingSystemResponse", propOrder = {
    "billingProfile"
})
public class GetBillingSystemResponse {

    @XmlElement(required = true)
    protected BillingProfileType billingProfile;

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
