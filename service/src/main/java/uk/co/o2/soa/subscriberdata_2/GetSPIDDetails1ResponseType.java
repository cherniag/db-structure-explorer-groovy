
package uk.co.o2.soa.subscriberdata_2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Complex type definition for getSPIDDetails_1 response element.
 * 
 * <p>Java class for getSPIDDetails_1ResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getSPIDDetails_1ResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="serviceProviderDetails" type="{http://soa.o2.co.uk/subscriberdata_2}serviceProviderDetailsType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getSPIDDetails_1ResponseType", propOrder = {
    "serviceProviderDetails"
})
public class GetSPIDDetails1ResponseType {

    @XmlElement(required = true)
    protected ServiceProviderDetailsType serviceProviderDetails;

    /**
     * Gets the value of the serviceProviderDetails property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceProviderDetailsType }
     *     
     */
    public ServiceProviderDetailsType getServiceProviderDetails() {
        return serviceProviderDetails;
    }

    /**
     * Sets the value of the serviceProviderDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceProviderDetailsType }
     *     
     */
    public void setServiceProviderDetails(ServiceProviderDetailsType value) {
        this.serviceProviderDetails = value;
    }

}
