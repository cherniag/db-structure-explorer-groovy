
package uk.co.o2.soa.coredata_1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *       		The standard address format in line with the latest PAF standards.
 *       		
 * 
 * <p>Java class for addressType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="addressType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="addressKey" type="{http://soa.o2.co.uk/coredata_1}addressKeyType" minOccurs="0"/>
 *         &lt;element name="organisationName" type="{http://soa.o2.co.uk/coredata_1}organisationNameType" minOccurs="0"/>
 *         &lt;element name="departmentName" type="{http://soa.o2.co.uk/coredata_1}departmentNameType" minOccurs="0"/>
 *         &lt;element name="subBuildingName" type="{http://soa.o2.co.uk/coredata_1}subBuildingNameType" minOccurs="0"/>
 *         &lt;element name="buildingName" type="{http://soa.o2.co.uk/coredata_1}buildingNameType" minOccurs="0"/>
 *         &lt;element name="buildingNumber" type="{http://soa.o2.co.uk/coredata_1}buildingNumberType" minOccurs="0"/>
 *         &lt;element name="dependentThoroughfareNameAndDescriptor" type="{http://soa.o2.co.uk/coredata_1}thoroughfareNameAndDescriptorType" minOccurs="0"/>
 *         &lt;element name="thoroughfareNameAndDescriptor" type="{http://soa.o2.co.uk/coredata_1}thoroughfareNameAndDescriptorType" minOccurs="0"/>
 *         &lt;element name="doubleDependentLocality" type="{http://soa.o2.co.uk/coredata_1}dependentLocalityType" minOccurs="0"/>
 *         &lt;element name="dependentLocality" type="{http://soa.o2.co.uk/coredata_1}dependentLocalityType" minOccurs="0"/>
 *         &lt;element name="postTown" type="{http://soa.o2.co.uk/coredata_1}postTownType" minOccurs="0"/>
 *         &lt;element name="county" type="{http://soa.o2.co.uk/coredata_1}countyType" minOccurs="0"/>
 *         &lt;element name="postCode" type="{http://soa.o2.co.uk/coredata_1}postcodeType" minOccurs="0"/>
 *         &lt;element name="poBoxNumber" type="{http://soa.o2.co.uk/coredata_1}poBoxNumberType" minOccurs="0"/>
 *         &lt;element name="countryCode" type="{http://soa.o2.co.uk/coredata_1}countryCodeType" minOccurs="0"/>
 *         &lt;element name="addressMetaData" type="{http://soa.o2.co.uk/coredata_1}addressMetaDataType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addressType", propOrder = {
    "addressKey",
    "organisationName",
    "departmentName",
    "subBuildingName",
    "buildingName",
    "buildingNumber",
    "dependentThoroughfareNameAndDescriptor",
    "thoroughfareNameAndDescriptor",
    "doubleDependentLocality",
    "dependentLocality",
    "postTown",
    "county",
    "postCode",
    "poBoxNumber",
    "countryCode",
    "addressMetaData"
})
public class AddressType {

    protected String addressKey;
    protected String organisationName;
    protected String departmentName;
    protected String subBuildingName;
    protected String buildingName;
    protected String buildingNumber;
    protected String dependentThoroughfareNameAndDescriptor;
    protected String thoroughfareNameAndDescriptor;
    protected String doubleDependentLocality;
    protected String dependentLocality;
    protected String postTown;
    protected String county;
    protected String postCode;
    protected String poBoxNumber;
    protected String countryCode;
    protected List<AddressMetaDataType> addressMetaData;

    /**
     * Gets the value of the addressKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddressKey() {
        return addressKey;
    }

    /**
     * Sets the value of the addressKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddressKey(String value) {
        this.addressKey = value;
    }

    /**
     * Gets the value of the organisationName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganisationName() {
        return organisationName;
    }

    /**
     * Sets the value of the organisationName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganisationName(String value) {
        this.organisationName = value;
    }

    /**
     * Gets the value of the departmentName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * Sets the value of the departmentName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepartmentName(String value) {
        this.departmentName = value;
    }

    /**
     * Gets the value of the subBuildingName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubBuildingName() {
        return subBuildingName;
    }

    /**
     * Sets the value of the subBuildingName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubBuildingName(String value) {
        this.subBuildingName = value;
    }

    /**
     * Gets the value of the buildingName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBuildingName() {
        return buildingName;
    }

    /**
     * Sets the value of the buildingName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBuildingName(String value) {
        this.buildingName = value;
    }

    /**
     * Gets the value of the buildingNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBuildingNumber() {
        return buildingNumber;
    }

    /**
     * Sets the value of the buildingNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBuildingNumber(String value) {
        this.buildingNumber = value;
    }

    /**
     * Gets the value of the dependentThoroughfareNameAndDescriptor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDependentThoroughfareNameAndDescriptor() {
        return dependentThoroughfareNameAndDescriptor;
    }

    /**
     * Sets the value of the dependentThoroughfareNameAndDescriptor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDependentThoroughfareNameAndDescriptor(String value) {
        this.dependentThoroughfareNameAndDescriptor = value;
    }

    /**
     * Gets the value of the thoroughfareNameAndDescriptor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getThoroughfareNameAndDescriptor() {
        return thoroughfareNameAndDescriptor;
    }

    /**
     * Sets the value of the thoroughfareNameAndDescriptor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setThoroughfareNameAndDescriptor(String value) {
        this.thoroughfareNameAndDescriptor = value;
    }

    /**
     * Gets the value of the doubleDependentLocality property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDoubleDependentLocality() {
        return doubleDependentLocality;
    }

    /**
     * Sets the value of the doubleDependentLocality property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDoubleDependentLocality(String value) {
        this.doubleDependentLocality = value;
    }

    /**
     * Gets the value of the dependentLocality property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDependentLocality() {
        return dependentLocality;
    }

    /**
     * Sets the value of the dependentLocality property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDependentLocality(String value) {
        this.dependentLocality = value;
    }

    /**
     * Gets the value of the postTown property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostTown() {
        return postTown;
    }

    /**
     * Sets the value of the postTown property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostTown(String value) {
        this.postTown = value;
    }

    /**
     * Gets the value of the county property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCounty() {
        return county;
    }

    /**
     * Sets the value of the county property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCounty(String value) {
        this.county = value;
    }

    /**
     * Gets the value of the postCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * Sets the value of the postCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostCode(String value) {
        this.postCode = value;
    }

    /**
     * Gets the value of the poBoxNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPoBoxNumber() {
        return poBoxNumber;
    }

    /**
     * Sets the value of the poBoxNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPoBoxNumber(String value) {
        this.poBoxNumber = value;
    }

    /**
     * Gets the value of the countryCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the value of the countryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryCode(String value) {
        this.countryCode = value;
    }

    /**
     * Gets the value of the addressMetaData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the addressMetaData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAddressMetaData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AddressMetaDataType }
     * 
     * 
     */
    public List<AddressMetaDataType> getAddressMetaData() {
        if (addressMetaData == null) {
            addressMetaData = new ArrayList<AddressMetaDataType>();
        }
        return this.addressMetaData;
    }

}
