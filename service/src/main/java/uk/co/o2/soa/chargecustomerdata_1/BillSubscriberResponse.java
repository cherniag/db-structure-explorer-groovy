
package uk.co.o2.soa.chargecustomerdata_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Response for billSubscriber operation
 * 
 * <p>Java class for billSubscriberResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="billSubscriberResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="result" type="{http://soa.o2.co.uk/chargecustomerdata_1}serviceResult"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "billSubscriberResponse", propOrder = {
    "result"
})
public class BillSubscriberResponse {

    @XmlElement(required = true)
    protected ServiceResult result;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceResult }
     *     
     */
    public ServiceResult getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceResult }
     *     
     */
    public void setResult(ServiceResult value) {
        this.result = value;
    }

	@Override
	public String toString() {
		return "BillSubscriberResponse [result=" + result + "]";
	}

}
