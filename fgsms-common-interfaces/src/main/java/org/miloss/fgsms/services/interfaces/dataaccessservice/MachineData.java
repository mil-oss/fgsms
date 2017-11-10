
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.DriveInformation;
import org.miloss.fgsms.services.interfaces.common.MachinePerformanceData;
import org.miloss.fgsms.services.interfaces.common.ProcessPerformanceData;


/**
 * <p>Java class for MachineData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MachineData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="driveInformation" type="{urn:org:miloss:fgsms:services:interfaces:common}driveInformation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Hostname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DomainName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MachinePerformanceData" type="{urn:org:miloss:fgsms:services:interfaces:common}MachinePerformanceData"/>
 *         &lt;element name="ProcessPerformanceData" type="{urn:org:miloss:fgsms:services:interfaces:common}ProcessPerformanceData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MachineData", propOrder = {
    "driveInformation",
    "hostname",
    "domainName",
    "machinePerformanceData",
    "processPerformanceData"
})
public class MachineData
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    protected List<DriveInformation> driveInformation;
    @XmlElement(name = "Hostname", required = true)
    protected String hostname;
    @XmlElement(name = "DomainName", required = true)
    protected String domainName;
    @XmlElement(name = "MachinePerformanceData", required = true)
    protected MachinePerformanceData machinePerformanceData;
    @XmlElement(name = "ProcessPerformanceData")
    protected List<ProcessPerformanceData> processPerformanceData;

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
     * Gets the value of the domainName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomainName() {
        return domainName;
    }

    /**
     * Sets the value of the domainName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomainName(String value) {
        this.domainName = value;
    }

    public boolean isSetDomainName() {
        return (this.domainName!= null);
    }

    /**
     * Gets the value of the machinePerformanceData property.
     * 
     * @return
     *     possible object is
     *     {@link MachinePerformanceData }
     *     
     */
    public MachinePerformanceData getMachinePerformanceData() {
        return machinePerformanceData;
    }

    /**
     * Sets the value of the machinePerformanceData property.
     * 
     * @param value
     *     allowed object is
     *     {@link MachinePerformanceData }
     *     
     */
    public void setMachinePerformanceData(MachinePerformanceData value) {
        this.machinePerformanceData = value;
    }

    public boolean isSetMachinePerformanceData() {
        return (this.machinePerformanceData!= null);
    }

    /**
     * Gets the value of the processPerformanceData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the processPerformanceData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcessPerformanceData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProcessPerformanceData }
     * 
     * 
     */
    public List<ProcessPerformanceData> getProcessPerformanceData() {
        if (processPerformanceData == null) {
            processPerformanceData = new ArrayList<ProcessPerformanceData>();
        }
        return this.processPerformanceData;
    }

    public boolean isSetProcessPerformanceData() {
        return ((this.processPerformanceData!= null)&&(!this.processPerformanceData.isEmpty()));
    }

    public void unsetProcessPerformanceData() {
        this.processPerformanceData = null;
    }

}
