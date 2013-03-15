
package uk.co.o2.soa.coredata;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				An individual Item that carries key-value pair for
 * 				extensible properties
 * 			
 * 
 * <p>Java class for MapItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MapItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;choice>
 *           &lt;element name="intValue" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *           &lt;element name="strValue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *           &lt;element name="boolValue" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MapItem", propOrder = {
    "key",
    "intValue",
    "strValue",
    "boolValue"
})
public class MapItem {

    @XmlElement(required = true)
    protected String key;
    protected BigInteger intValue;
    protected String strValue;
    protected Boolean boolValue;

    /**
     * Gets the value of the key property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKey(String value) {
        this.key = value;
    }

    /**
     * Gets the value of the intValue property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIntValue() {
        return intValue;
    }

    /**
     * Sets the value of the intValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIntValue(BigInteger value) {
        this.intValue = value;
    }

    /**
     * Gets the value of the strValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStrValue() {
        return strValue;
    }

    /**
     * Sets the value of the strValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStrValue(String value) {
        this.strValue = value;
    }

    /**
     * Gets the value of the boolValue property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBoolValue() {
        return boolValue;
    }

    /**
     * Sets the value of the boolValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBoolValue(Boolean value) {
        this.boolValue = value;
    }

}
