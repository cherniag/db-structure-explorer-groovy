
package uk.co.o2.soa.subscriberdata_2;

import javax.xml.bind.annotation.*;

import uk.co.o2.soa.coredata.SegmentType;


/**
 * Only returns Segment
 * 
 * <p>Java class for getSubscriberSegmentResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getSubscriberSegmentResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Segment" type="{http://soa.o2.co.uk/coredata_1}SegmentType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getSubscriberSegmentResponse", propOrder = {
    "segment"
})
public class GetSubscriberSegmentResponse {

    @XmlElement(name = "Segment", required = true)
    protected SegmentType segment;

    /**
     * Gets the value of the segment property.
     * 
     * @return
     *     possible object is
     *     {@link SegmentType }
     *     
     */
    public SegmentType getSegment() {
        return segment;
    }

    /**
     * Sets the value of the segment property.
     * 
     * @param value
     *     allowed object is
     *     {@link SegmentType }
     *     
     */
    public void setSegment(SegmentType value) {
        this.segment = value;
    }

}
