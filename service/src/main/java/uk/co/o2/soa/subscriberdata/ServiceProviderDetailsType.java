
package uk.co.o2.soa.subscriberdata;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Complex type definition having various basic service provider details.
 * 
 * <p>Java class for serviceProviderDetailsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="serviceProviderDetailsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="serviceProviderId" type="{http://soa.o2.co.uk/coredata_1}spidType"/>
 *         &lt;element name="serviceProviderName" type="{http://soa.o2.co.uk/subscriberdata_2}serviceProviderNameType"/>
 *         &lt;element name="masterServiceProviderId" type="{http://soa.o2.co.uk/coredata_1}spidType"/>
 *         &lt;element name="autoReleaseCredit" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="autoReleaseReserved" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="autoReleaseStln" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="barAll" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="creationDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="nextDeliveryDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="deliveryFrequency" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="aliasId" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="comments" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="csercsRecordLength" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="csercsFileNextProvidedDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="processingPeriod" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="heldIndicator" type="{http://soa.o2.co.uk/subscriberdata_2}singleCharType" minOccurs="0"/>
 *         &lt;element name="testIndicator" type="{http://soa.o2.co.uk/subscriberdata_2}singleCharType" minOccurs="0"/>
 *         &lt;element name="pricedCallDistributionPriority" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="mnoGroup" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="postpayOrPrepay" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="machIMSICategory" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "serviceProviderDetailsType", propOrder = {
    "serviceProviderId",
    "serviceProviderName",
    "masterServiceProviderId",
    "autoReleaseCredit",
    "autoReleaseReserved",
    "autoReleaseStln",
    "barAll",
    "creationDateTime",
    "nextDeliveryDateTime",
    "deliveryFrequency",
    "aliasId",
    "comments",
    "csercsRecordLength",
    "csercsFileNextProvidedDate",
    "processingPeriod",
    "heldIndicator",
    "testIndicator",
    "pricedCallDistributionPriority",
    "mnoGroup",
    "postpayOrPrepay",
    "machIMSICategory"
})
public class ServiceProviderDetailsType {

    @XmlElement(required = true)
    protected String serviceProviderId;
    @XmlElement(required = true)
    protected String serviceProviderName;
    @XmlElement(required = true)
    protected String masterServiceProviderId;
    protected boolean autoReleaseCredit;
    protected boolean autoReleaseReserved;
    protected boolean autoReleaseStln;
    protected boolean barAll;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creationDateTime;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar nextDeliveryDateTime;
    @XmlElement(required = true)
    protected BigInteger deliveryFrequency;
    protected BigInteger aliasId;
    protected String comments;
    protected String csercsRecordLength;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar csercsFileNextProvidedDate;
    protected BigInteger processingPeriod;
    protected String heldIndicator;
    protected String testIndicator;
    protected BigInteger pricedCallDistributionPriority;
    protected BigInteger mnoGroup;
    @XmlElement(required = true)
    protected String postpayOrPrepay;
    @XmlElement(required = true)
    protected String machIMSICategory;

    /**
     * Gets the value of the serviceProviderId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceProviderId() {
        return serviceProviderId;
    }

    /**
     * Sets the value of the serviceProviderId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceProviderId(String value) {
        this.serviceProviderId = value;
    }

    /**
     * Gets the value of the serviceProviderName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceProviderName() {
        return serviceProviderName;
    }

    /**
     * Sets the value of the serviceProviderName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceProviderName(String value) {
        this.serviceProviderName = value;
    }

    /**
     * Gets the value of the masterServiceProviderId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMasterServiceProviderId() {
        return masterServiceProviderId;
    }

    /**
     * Sets the value of the masterServiceProviderId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMasterServiceProviderId(String value) {
        this.masterServiceProviderId = value;
    }

    /**
     * Gets the value of the autoReleaseCredit property.
     * 
     */
    public boolean isAutoReleaseCredit() {
        return autoReleaseCredit;
    }

    /**
     * Sets the value of the autoReleaseCredit property.
     * 
     */
    public void setAutoReleaseCredit(boolean value) {
        this.autoReleaseCredit = value;
    }

    /**
     * Gets the value of the autoReleaseReserved property.
     * 
     */
    public boolean isAutoReleaseReserved() {
        return autoReleaseReserved;
    }

    /**
     * Sets the value of the autoReleaseReserved property.
     * 
     */
    public void setAutoReleaseReserved(boolean value) {
        this.autoReleaseReserved = value;
    }

    /**
     * Gets the value of the autoReleaseStln property.
     * 
     */
    public boolean isAutoReleaseStln() {
        return autoReleaseStln;
    }

    /**
     * Sets the value of the autoReleaseStln property.
     * 
     */
    public void setAutoReleaseStln(boolean value) {
        this.autoReleaseStln = value;
    }

    /**
     * Gets the value of the barAll property.
     * 
     */
    public boolean isBarAll() {
        return barAll;
    }

    /**
     * Sets the value of the barAll property.
     * 
     */
    public void setBarAll(boolean value) {
        this.barAll = value;
    }

    /**
     * Gets the value of the creationDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreationDateTime() {
        return creationDateTime;
    }

    /**
     * Sets the value of the creationDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreationDateTime(XMLGregorianCalendar value) {
        this.creationDateTime = value;
    }

    /**
     * Gets the value of the nextDeliveryDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getNextDeliveryDateTime() {
        return nextDeliveryDateTime;
    }

    /**
     * Sets the value of the nextDeliveryDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setNextDeliveryDateTime(XMLGregorianCalendar value) {
        this.nextDeliveryDateTime = value;
    }

    /**
     * Gets the value of the deliveryFrequency property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDeliveryFrequency() {
        return deliveryFrequency;
    }

    /**
     * Sets the value of the deliveryFrequency property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDeliveryFrequency(BigInteger value) {
        this.deliveryFrequency = value;
    }

    /**
     * Gets the value of the aliasId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAliasId() {
        return aliasId;
    }

    /**
     * Sets the value of the aliasId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAliasId(BigInteger value) {
        this.aliasId = value;
    }

    /**
     * Gets the value of the comments property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComments() {
        return comments;
    }

    /**
     * Sets the value of the comments property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComments(String value) {
        this.comments = value;
    }

    /**
     * Gets the value of the csercsRecordLength property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCsercsRecordLength() {
        return csercsRecordLength;
    }

    /**
     * Sets the value of the csercsRecordLength property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCsercsRecordLength(String value) {
        this.csercsRecordLength = value;
    }

    /**
     * Gets the value of the csercsFileNextProvidedDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCsercsFileNextProvidedDate() {
        return csercsFileNextProvidedDate;
    }

    /**
     * Sets the value of the csercsFileNextProvidedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCsercsFileNextProvidedDate(XMLGregorianCalendar value) {
        this.csercsFileNextProvidedDate = value;
    }

    /**
     * Gets the value of the processingPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getProcessingPeriod() {
        return processingPeriod;
    }

    /**
     * Sets the value of the processingPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setProcessingPeriod(BigInteger value) {
        this.processingPeriod = value;
    }

    /**
     * Gets the value of the heldIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeldIndicator() {
        return heldIndicator;
    }

    /**
     * Sets the value of the heldIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeldIndicator(String value) {
        this.heldIndicator = value;
    }

    /**
     * Gets the value of the testIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTestIndicator() {
        return testIndicator;
    }

    /**
     * Sets the value of the testIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTestIndicator(String value) {
        this.testIndicator = value;
    }

    /**
     * Gets the value of the pricedCallDistributionPriority property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPricedCallDistributionPriority() {
        return pricedCallDistributionPriority;
    }

    /**
     * Sets the value of the pricedCallDistributionPriority property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPricedCallDistributionPriority(BigInteger value) {
        this.pricedCallDistributionPriority = value;
    }

    /**
     * Gets the value of the mnoGroup property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMnoGroup() {
        return mnoGroup;
    }

    /**
     * Sets the value of the mnoGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMnoGroup(BigInteger value) {
        this.mnoGroup = value;
    }

    /**
     * Gets the value of the postpayOrPrepay property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostpayOrPrepay() {
        return postpayOrPrepay;
    }

    /**
     * Sets the value of the postpayOrPrepay property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostpayOrPrepay(String value) {
        this.postpayOrPrepay = value;
    }

    /**
     * Gets the value of the machIMSICategory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMachIMSICategory() {
        return machIMSICategory;
    }

    /**
     * Sets the value of the machIMSICategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMachIMSICategory(String value) {
        this.machIMSICategory = value;
    }

}
