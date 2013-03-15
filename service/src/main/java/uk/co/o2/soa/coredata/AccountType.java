
package uk.co.o2.soa.coredata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Data type to define a customers account. This could be
 * 				either a portal account, msisdn, or a companion account
 * 				and msisdn. Or a Bank Account program ID and account ID
 * 				(initially for Timberlake)
 * 			
 * 
 * <p>Java class for accountType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accountType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="portalID" type="{http://soa.o2.co.uk/coredata_1}portalIDType"/>
 *         &lt;element name="msisdn" type="{http://soa.o2.co.uk/coredata_1}msisdnType"/>
 *         &lt;element name="accountDetail" type="{http://soa.o2.co.uk/coredata_1}accountDetail"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "accountType", propOrder = {
    "portalID",
    "msisdn",
    "accountDetail"
})
public class AccountType {

    protected String portalID;
    protected String msisdn;
    protected AccountDetail accountDetail;

    /**
     * Gets the value of the portalID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPortalID() {
        return portalID;
    }

    /**
     * Sets the value of the portalID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPortalID(String value) {
        this.portalID = value;
    }

    /**
     * Gets the value of the msisdn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the value of the msisdn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsisdn(String value) {
        this.msisdn = value;
    }

    /**
     * Gets the value of the accountDetail property.
     * 
     * @return
     *     possible object is
     *     {@link AccountDetail }
     *     
     */
    public AccountDetail getAccountDetail() {
        return accountDetail;
    }

    /**
     * Sets the value of the accountDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccountDetail }
     *     
     */
    public void setAccountDetail(AccountDetail value) {
        this.accountDetail = value;
    }

}
