
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.DriveInformation;
import org.miloss.fgsms.services.interfaces.common.NetworkAdapterInfo;


/**
 * container for machine specific information
 * 
 * <p>Java class for MachineInformation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MachineInformation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="hostname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="uri" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="addresses" type="{urn:org:miloss:fgsms:services:interfaces:common}NetworkAdapterInfo" maxOccurs="unbounded"/>
 *         &lt;element name="domain" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="operatingsystem" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cpucorecount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="memoryinstalled" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="driveInformation" type="{urn:org:miloss:fgsms:services:interfaces:common}driveInformation" maxOccurs="unbounded"/>
 *         &lt;element name="PropertyPair" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}PropertyPair" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MachineInformation", propOrder = {
    "hostname",
    "uri",
    "addresses",
    "domain",
    "operatingsystem",
    "cpucorecount",
    "memoryinstalled",
    "driveInformation",
    "propertyPair"
})
@XmlRootElement
public class MachineInformation
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected String hostname;
    @XmlElement(required = true)
    protected String uri;
    @XmlElement(required = true, nillable = true)
    protected List<NetworkAdapterInfo> addresses;
    @XmlElement(required = true, nillable = true)
    protected String domain;
    @XmlElement(required = true, nillable = true)
    protected String operatingsystem;
    protected long cpucorecount;
    protected long memoryinstalled;
    @XmlElement(required = true, nillable = true)
    protected List<DriveInformation> driveInformation;
    @XmlElement(name = "PropertyPair", required = true, nillable = true)
    protected List<PropertyPair> propertyPair;

    /**
     * Gets the value of the hostname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Sets the value of the hostname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHostname(String value) {
        this.hostname = value;
    }

    public boolean isSetHostname() {
        return (this.hostname!= null);
    }

    /**
     * Gets the value of the uri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets the value of the uri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUri(String value) {
        this.uri = value;
    }

    public boolean isSetUri() {
        return (this.uri!= null);
    }

    /**
     * Gets the value of the addresses property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the addresses property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAddresses().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NetworkAdapterInfo }
     * 
     * 
     */
    public List<NetworkAdapterInfo> getAddresses() {
        if (addresses == null) {
            addresses = new ArrayList<NetworkAdapterInfo>();
        }
        return this.addresses;
    }

    public boolean isSetAddresses() {
        return ((this.addresses!= null)&&(!this.addresses.isEmpty()));
    }

    public void unsetAddresses() {
        this.addresses = null;
    }

    /**
     * Gets the value of the domain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets the value of the domain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomain(String value) {
        this.domain = value;
    }

    public boolean isSetDomain() {
        return (this.domain!= null);
    }

    /**
     * Gets the value of the operatingsystem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperatingsystem() {
        return operatingsystem;
    }

    /**
     * Sets the value of the operatingsystem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperatingsystem(String value) {
        this.operatingsystem = value;
    }

    public boolean isSetOperatingsystem() {
        return (this.operatingsystem!= null);
    }

    /**
     * Gets the value of the cpucorecount property.
     * 
     */
    public long getCpucorecount() {
        return cpucorecount;
    }

    /**
     * Sets the value of the cpucorecount property.
     * 
     */
    public void setCpucorecount(long value) {
        this.cpucorecount = value;
    }

    public boolean isSetCpucorecount() {
        return true;
    }

    /**
     * Gets the value of the memoryinstalled property.
     * 
     */
    public long getMemoryinstalled() {
        return memoryinstalled;
    }

    /**
     * Sets the value of the memoryinstalled property.
     * 
     */
    public void setMemoryinstalled(long value) {
        this.memoryinstalled = value;
    }

    public boolean isSetMemoryinstalled() {
        return true;
    }

    /**
     * Gets the value of the driveInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the driveInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDriveInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DriveInformation }
     * 
     * 
     */
    public List<DriveInformation> getDriveInformation() {
        if (driveInformation == null) {
            driveInformation = new ArrayList<DriveInformation>();
        }
        return this.driveInformation;
    }

    public boolean isSetDriveInformation() {
        return ((this.driveInformation!= null)&&(!this.driveInformation.isEmpty()));
    }

    public void unsetDriveInformation() {
        this.driveInformation = null;
    }

    /**
     * Gets the value of the propertyPair property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the propertyPair property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPropertyPair().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PropertyPair }
     * 
     * 
     */
    public List<PropertyPair> getPropertyPair() {
        if (propertyPair == null) {
            propertyPair = new ArrayList<PropertyPair>();
        }
        return this.propertyPair;
    }

    public boolean isSetPropertyPair() {
        return ((this.propertyPair!= null)&&(!this.propertyPair.isEmpty()));
    }

    public void unsetPropertyPair() {
        this.propertyPair = null;
    }

}
