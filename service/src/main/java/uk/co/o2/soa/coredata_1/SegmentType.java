
package uk.co.o2.soa.coredata_1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SegmentType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SegmentType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Consumer"/>
 *     &lt;enumeration value="SME"/>
 *     &lt;enumeration value="Corporate"/>
 *     &lt;enumeration value="ISP"/>
 *     &lt;enumeration value="Unknown"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SegmentType")
@XmlEnum
public enum SegmentType {

    @XmlEnumValue("Consumer")
    CONSUMER("Consumer"),
    SME("SME"),
    @XmlEnumValue("Corporate")
    CORPORATE("Corporate"),
    ISP("ISP"),
    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown");
    private final String value;

    SegmentType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SegmentType fromValue(String v) {
        for (SegmentType c: SegmentType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
