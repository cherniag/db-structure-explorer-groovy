
package uk.co.o2.soa.coredata_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				The type of the response sent to the calling
 * 				application, if something goes wrong.
 * 			
 * 
 * <p>Java class for SOAFaultType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SOAFaultType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://soa.o2.co.uk/coredata_1}SOAFaultOriginator"/>
 *         &lt;element ref="{http://soa.o2.co.uk/coredata_1}SOAFaultCode"/>
 *         &lt;element name="faultDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="faultTrace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element ref="{http://soa.o2.co.uk/coredata_1}SOATransactionID" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SOAFaultType", propOrder = {
    "soaFaultOriginator",
    "soaFaultCode",
    "faultDescription",
    "faultTrace",
    "soaTransactionID"
})
public class SOAFaultType {

    @XmlElement(name = "SOAFaultOriginator", required = true)
    protected String soaFaultOriginator;
    @XmlElement(name = "SOAFaultCode", required = true)
    protected String soaFaultCode;
    protected String faultDescription;
    protected String faultTrace;
    @XmlElement(name = "SOATransactionID")
    protected String soaTransactionID;

    /**
     * Gets the value of the soaFaultOriginator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSOAFaultOriginator() {
        return soaFaultOriginator;
    }

    /**
     * Sets the value of the soaFaultOriginator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSOAFaultOriginator(String value) {
        this.soaFaultOriginator = value;
    }

    /**
     * Gets the value of the soaFaultCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSOAFaultCode() {
        return soaFaultCode;
    }

    /**
     * Sets the value of the soaFaultCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSOAFaultCode(String value) {
        this.soaFaultCode = value;
    }

    /**
     * Gets the value of the faultDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFaultDescription() {
        return faultDescription;
    }

    /**
     * Sets the value of the faultDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFaultDescription(String value) {
        this.faultDescription = value;
    }

    /**
     * Gets the value of the faultTrace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFaultTrace() {
        return faultTrace;
    }

    /**
     * Sets the value of the faultTrace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFaultTrace(String value) {
        this.faultTrace = value;
    }

    /**
     * Gets the value of the soaTransactionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSOATransactionID() {
        return soaTransactionID;
    }

    /**
     * Sets the value of the soaTransactionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSOATransactionID(String value) {
        this.soaTransactionID = value;
    }

}
