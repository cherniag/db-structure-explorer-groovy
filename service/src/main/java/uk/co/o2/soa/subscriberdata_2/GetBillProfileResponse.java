
package uk.co.o2.soa.subscriberdata_2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Returns the Billing System, account number and list of MSISDNs
 * 
 * <p>Java class for getBillProfileResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getBillProfileResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="billingSystem" type="{http://soa.o2.co.uk/subscriberdata_2}BillingProfileType"/>
 *         &lt;element name="accountNumber" type="{http://soa.o2.co.uk/coredata_1}billingAccountNumberType"/>
 *         &lt;element name="msisdnList" type="{http://soa.o2.co.uk/subscriberdata_2}msisdnList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getBillProfileResponse", propOrder = {
    "billingSystem",
    "accountNumber",
    "msisdnList"
})
public class GetBillProfileResponse {

    @XmlElement(required = true)
    protected BillingProfileType billingSystem;
    @XmlElement(required = true)
    protected String accountNumber;
    protected MsisdnList msisdnList;

    /**
     * Gets the value of the billingSystem property.
     * 
     * @return
     *     possible object is
     *     {@link BillingProfileType }
     *     
     */
    public BillingProfileType getBillingSystem() {
        return billingSystem;
    }

    /**
     * Sets the value of the billingSystem property.
     * 
     * @param value
     *     allowed object is
     *     {@link BillingProfileType }
     *     
     */
    public void setBillingSystem(BillingProfileType value) {
        this.billingSystem = value;
    }

    /**
     * Gets the value of the accountNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the value of the accountNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountNumber(String value) {
        this.accountNumber = value;
    }

    /**
     * Gets the value of the msisdnList property.
     * 
     * @return
     *     possible object is
     *     {@link MsisdnList }
     *     
     */
    public MsisdnList getMsisdnList() {
        return msisdnList;
    }

    /**
     * Sets the value of the msisdnList property.
     * 
     * @param value
     *     allowed object is
     *     {@link MsisdnList }
     *     
     */
    public void setMsisdnList(MsisdnList value) {
        this.msisdnList = value;
    }

}
