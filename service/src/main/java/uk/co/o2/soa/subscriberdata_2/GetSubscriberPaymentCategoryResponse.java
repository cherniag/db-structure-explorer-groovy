
package uk.co.o2.soa.subscriberdata_2;

import javax.xml.bind.annotation.*;

import uk.co.o2.soa.coredata.PaymentCategoryType;

/**
 * returns prepay or postpay
 * 
 * <p>Java class for getSubscriberPaymentCategoryResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getSubscriberPaymentCategoryResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PaymentCategory" type="{http://soa.o2.co.uk/coredata_1}PaymentCategoryType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getSubscriberPaymentCategoryResponse", propOrder = {
    "paymentCategory"
})
public class GetSubscriberPaymentCategoryResponse {

    @XmlElement(name = "PaymentCategory", required = true)
    protected PaymentCategoryType paymentCategory;

    /**
     * Gets the value of the paymentCategory property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentCategoryType }
     *     
     */
    public PaymentCategoryType getPaymentCategory() {
        return paymentCategory;
    }

    /**
     * Sets the value of the paymentCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentCategoryType }
     *     
     */
    public void setPaymentCategory(PaymentCategoryType value) {
        this.paymentCategory = value;
    }

}
