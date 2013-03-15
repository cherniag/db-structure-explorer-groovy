
package uk.co.o2.soa.coredata;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				A generic extensible complex type that can be used to
 * 				send multiple name-value pairs
 * 			
 * 
 * <p>Java class for MapType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MapType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mapItem" type="{http://soa.o2.co.uk/coredata_1}MapItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MapType", propOrder = {
    "mapItem"
})
public class MapType {

    protected List<MapItem> mapItem;

    /**
     * Gets the value of the mapItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mapItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMapItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MapItem }
     * 
     * 
     */
    public List<MapItem> getMapItem() {
        if (mapItem == null) {
            mapItem = new ArrayList<MapItem>();
        }
        return this.mapItem;
    }

}
