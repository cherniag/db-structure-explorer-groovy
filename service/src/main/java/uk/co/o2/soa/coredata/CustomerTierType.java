
package uk.co.o2.soa.coredata;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for customerTierType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="customerTierType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Platinum"/>
 *     &lt;enumeration value="Gold"/>
 *     &lt;enumeration value="Silver"/>
 *     &lt;enumeration value="Bronze"/>
 *     &lt;enumeration value="Blue"/>
 *     &lt;enumeration value="Unknown"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "customerTierType")
@XmlEnum
public enum CustomerTierType {

    @XmlEnumValue("Platinum")
    PLATINUM("Platinum"),
    @XmlEnumValue("Gold")
    GOLD("Gold"),
    @XmlEnumValue("Silver")
    SILVER("Silver"),
    @XmlEnumValue("Bronze")
    BRONZE("Bronze"),
    @XmlEnumValue("Blue")
    BLUE("Blue"),
    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown");
    private final String value;

    CustomerTierType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CustomerTierType fromValue(String v) {
        for (CustomerTierType c: CustomerTierType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
