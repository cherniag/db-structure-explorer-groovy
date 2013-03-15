
package uk.co.o2.soa.subscriberdata_2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * The customer current tariff and the list of services present on the account. If this is provided then the customer data charging type 
 * 			would be determined using the tariff/services combination.
 * 
 * <p>Java class for productListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="productListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tariffProductId" type="{http://soa.o2.co.uk/coredata_1}productIDType"/>
 *         &lt;element name="services" type="{http://soa.o2.co.uk/subscriberdata_2}servicesType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "productListType", propOrder = {
    "tariffProductId",
    "services"
})
public class ProductListType {

    @XmlElement(required = true)
    protected String tariffProductId;
    protected ServicesType services;

    /**
     * Gets the value of the tariffProductId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTariffProductId() {
        return tariffProductId;
    }

    /**
     * Sets the value of the tariffProductId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTariffProductId(String value) {
        this.tariffProductId = value;
    }

    /**
     * Gets the value of the services property.
     * 
     * @return
     *     possible object is
     *     {@link ServicesType }
     *     
     */
    public ServicesType getServices() {
        return services;
    }

    /**
     * Sets the value of the services property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServicesType }
     *     
     */
    public void setServices(ServicesType value) {
        this.services = value;
    }

}
