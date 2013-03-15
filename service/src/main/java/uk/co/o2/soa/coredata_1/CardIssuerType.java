
package uk.co.o2.soa.coredata_1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cardIssuerType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="cardIssuerType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MAESTRO"/>
 *     &lt;enumeration value="SOLO"/>
 *     &lt;enumeration value="VISA"/>
 *     &lt;enumeration value="MASTERCARD"/>
 *     &lt;enumeration value="DELTA"/>
 *     &lt;enumeration value="AMEX"/>
 *     &lt;enumeration value="ELECTRON"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "cardIssuerType")
@XmlEnum
public enum CardIssuerType {

    MAESTRO,
    SOLO,
    VISA,
    MASTERCARD,
    DELTA,
    AMEX,
    ELECTRON;

    public String value() {
        return name();
    }

    public static CardIssuerType fromValue(String v) {
        return valueOf(v);
    }

}
