
package uk.co.o2.soa.coredata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PaymentCategoryType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PaymentCategoryType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Prepay"/>
 *     &lt;enumeration value="Postpay"/>
 *     &lt;enumeration value="Unknown"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PaymentCategoryType")
@XmlEnum
public enum PaymentCategoryType {

    @XmlEnumValue("Prepay")
    PREPAY("Prepay"),  //PAYG
    @XmlEnumValue("Postpay")
    POSTPAY("Postpay"),      //PAYM
    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown");   //PAYG
    private final String value;

    PaymentCategoryType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PaymentCategoryType fromValue(String v) {
        for (PaymentCategoryType c: PaymentCategoryType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
