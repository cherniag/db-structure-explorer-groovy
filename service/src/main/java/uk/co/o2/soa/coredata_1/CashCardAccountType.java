
package uk.co.o2.soa.coredata_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Uniquely identifies the account in the Prepaid Banking
 * 				system that is being referred to.
 * 			
 * 
 * <p>Java class for cashCardAccountType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cashCardAccountType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="programID" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="accountID" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="userID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="cardID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cashCardAccountType", propOrder = {
    "programID",
    "accountID",
    "userID",
    "cardID"
})
public class CashCardAccountType {

    protected long programID;
    protected long accountID;
    protected Long userID;
    protected Long cardID;

    /**
     * Gets the value of the programID property.
     * 
     */
    public long getProgramID() {
        return programID;
    }

    /**
     * Sets the value of the programID property.
     * 
     */
    public void setProgramID(long value) {
        this.programID = value;
    }

    /**
     * Gets the value of the accountID property.
     * 
     */
    public long getAccountID() {
        return accountID;
    }

    /**
     * Sets the value of the accountID property.
     * 
     */
    public void setAccountID(long value) {
        this.accountID = value;
    }

    /**
     * Gets the value of the userID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getUserID() {
        return userID;
    }

    /**
     * Sets the value of the userID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setUserID(Long value) {
        this.userID = value;
    }

    /**
     * Gets the value of the cardID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCardID() {
        return cardID;
    }

    /**
     * Sets the value of the cardID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCardID(Long value) {
        this.cardID = value;
    }

}
