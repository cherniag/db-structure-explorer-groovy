
package uk.co.o2.soa.chargecustomerdata_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Complex type for service result
 * 
 * <p>Java class for serviceResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="serviceResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="resultCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="resultDescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sagTransactionId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="applicationReference" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="resultData" type="{http://soa.o2.co.uk/chargecustomerdata_1}map" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "serviceResult", propOrder = {
    "resultCode",
    "resultDescription",
    "sagTransactionId",
    "applicationReference",
    "resultData"
})
public class ServiceResult {

    @XmlElement(required = true)
    protected String resultCode;
    @XmlElement(required = true)
    protected String resultDescription;
    @XmlElement(required = true)
    protected String sagTransactionId;
    @XmlElement(required = true)
    protected String applicationReference;
    protected Map resultData;

    /**
     * Gets the value of the resultCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultCode() {
        return resultCode;
    }

    /**
     * Sets the value of the resultCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultCode(String value) {
        this.resultCode = value;
    }

    /**
     * Gets the value of the resultDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultDescription() {
        return resultDescription;
    }

    /**
     * Sets the value of the resultDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultDescription(String value) {
        this.resultDescription = value;
    }

    /**
     * Gets the value of the sagTransactionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSagTransactionId() {
        return sagTransactionId;
    }

    /**
     * Sets the value of the sagTransactionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSagTransactionId(String value) {
        this.sagTransactionId = value;
    }

    /**
     * Gets the value of the applicationReference property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApplicationReference() {
        return applicationReference;
    }

    /**
     * Sets the value of the applicationReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApplicationReference(String value) {
        this.applicationReference = value;
    }

    /**
     * Gets the value of the resultData property.
     * 
     * @return
     *     possible object is
     *     {@link Map }
     *     
     */
    public Map getResultData() {
        return resultData;
    }

    /**
     * Sets the value of the resultData property.
     * 
     * @param value
     *     allowed object is
     *     {@link Map }
     *     
     */
    public void setResultData(Map value) {
        this.resultData = value;
    }

	@Override
	public String toString() {
		return "ServiceResult [applicationReference=" + applicationReference + ", sagTransactionId=" + sagTransactionId + ", resultCode=" + resultCode
				+ ", resultData=" + resultData + ", resultDescription=" + resultDescription + "]";
	}

}
