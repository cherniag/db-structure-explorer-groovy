
package uk.co.o2.soa.chargecustomerdata;

import java.math.BigInteger;
import javax.xml.bind.annotation.*;


/**
 * Request for billSubscriber operation
 * 
 * <p>Java class for billSubscriber complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="billSubscriber">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="msisdn" type="{http://soa.o2.co.uk/coredata_1}msisdnType"/>
 *         &lt;element name="subMerchantId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="priceGross" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="priceNet" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="debitCredit" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="contentCategory" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="contentType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="contentDescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="applicationReference" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="smsNotify" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="smsMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="promotionCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "billSubscriber", namespace="http://soa.o2.co.uk/chargecustomerdata_1")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "billSubscriber", propOrder = {
    "msisdn",
    "subMerchantId",
    "priceGross",
    "priceNet",
    "debitCredit",
    "contentCategory",
    "contentType",
    "contentDescription",
    "applicationReference",
    "smsNotify",
    "smsMessage",
    "promotionCode"
})
public class BillSubscriber {

    @XmlElement(required = true)
    protected String msisdn;
    @XmlElement(required = true)
    protected String subMerchantId;
    @XmlElement(required = true)
    protected BigInteger priceGross;
    @XmlElement(required = true)
    protected BigInteger priceNet;
    @XmlElement(required = true)
    protected String debitCredit;
    @XmlElement(required = true)
    protected String contentCategory;
    @XmlElement(required = true)
    protected String contentType;
    @XmlElement(required = true)
    protected String contentDescription;
    @XmlElement(required = true)
    protected String applicationReference;
    protected boolean smsNotify;
    @XmlElement(required = true)
    protected String smsMessage;
    @XmlElement(required = true)
    protected String promotionCode;

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
     * Gets the value of the subMerchantId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubMerchantId() {
        return subMerchantId;
    }

    /**
     * Sets the value of the subMerchantId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubMerchantId(String value) {
        this.subMerchantId = value;
    }

    /**
     * Gets the value of the priceGross property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPriceGross() {
        return priceGross;
    }

    /**
     * Sets the value of the priceGross property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPriceGross(BigInteger value) {
        this.priceGross = value;
    }

    /**
     * Gets the value of the priceNet property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPriceNet() {
        return priceNet;
    }

    /**
     * Sets the value of the priceNet property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPriceNet(BigInteger value) {
        this.priceNet = value;
    }

    /**
     * Gets the value of the debitCredit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDebitCredit() {
        return debitCredit;
    }

    /**
     * Sets the value of the debitCredit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDebitCredit(String value) {
        this.debitCredit = value;
    }

    /**
     * Gets the value of the contentCategory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContentCategory() {
        return contentCategory;
    }

    /**
     * Sets the value of the contentCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContentCategory(String value) {
        this.contentCategory = value;
    }

    /**
     * Gets the value of the contentType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the value of the contentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContentType(String value) {
        this.contentType = value;
    }

    /**
     * Gets the value of the contentDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContentDescription() {
        return contentDescription;
    }

    /**
     * Sets the value of the contentDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContentDescription(String value) {
        this.contentDescription = value;
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
     * Gets the value of the smsNotify property.
     * 
     */
    public boolean isSmsNotify() {
        return smsNotify;
    }

    /**
     * Sets the value of the smsNotify property.
     * 
     */
    public void setSmsNotify(boolean value) {
        this.smsNotify = value;
    }

    /**
     * Gets the value of the smsMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSmsMessage() {
        return smsMessage;
    }

    /**
     * Sets the value of the smsMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSmsMessage(String value) {
        this.smsMessage = value;
    }

    /**
     * Gets the value of the promotionCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPromotionCode() {
        return promotionCode;
    }

    /**
     * Sets the value of the promotionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPromotionCode(String value) {
        this.promotionCode = value;
    }

	@Override
	public String toString() {
		return "BillSubscriber [msisdn=" + msisdn + ", applicationReference=" + applicationReference + ", priceGross=" + priceGross + ", priceNet=" + priceNet
				+ ", smsNotify=" + smsNotify + ", smsMessage=" + smsMessage + ", contentCategory=" + contentCategory + ", contentDescription="
				+ contentDescription + ", contentType=" + contentType + ", debitCredit=" + debitCredit + ", promotionCode=" + promotionCode
				+ ", subMerchantId=" + subMerchantId + "]";
    }

}
