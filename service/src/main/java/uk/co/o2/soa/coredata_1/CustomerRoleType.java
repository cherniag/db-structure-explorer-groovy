
package uk.co.o2.soa.coredata_1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for customerRoleType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="customerRoleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="admin"/>
 *     &lt;enumeration value="user"/>
 *     &lt;enumeration value="chooser"/>
 *     &lt;enumeration value="superuser"/>
 *     &lt;enumeration value="unknown"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "customerRoleType")
@XmlEnum
public enum CustomerRoleType {

    @XmlEnumValue("admin")
    ADMIN("admin"),
    @XmlEnumValue("user")
    USER("user"),
    @XmlEnumValue("chooser")
    CHOOSER("chooser"),
    @XmlEnumValue("superuser")
    SUPERUSER("superuser"),
    @XmlEnumValue("unknown")
    UNKNOWN("unknown");
    private final String value;

    CustomerRoleType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CustomerRoleType fromValue(String v) {
        for (CustomerRoleType c: CustomerRoleType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
