
package org.miloss.fgsms.services.interfaces.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Calendar;


/**
 * <p>Java class for MachinePerformanceData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MachinePerformanceData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="uri" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="operationalstatus" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="statusmessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="NetworkAdapterPerformanceData" type="{urn:org:miloss:fgsms:services:interfaces:common}NetworkAdapterPerformanceData" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="driveInformation" type="{urn:org:miloss:fgsms:services:interfaces:common}driveInformation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="bytesusedMemory" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="percentusedCPU" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="numberofActiveThreads" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="startedAt" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MachinePerformanceData", propOrder = {
    "id",
    "uri",
    "operationalstatus",
    "statusmessage",
    "networkAdapterPerformanceData",
    "driveInformation",
    "bytesusedMemory",
    "percentusedCPU",
    "numberofActiveThreads",
    "timestamp",
    "startedAt"
})
public class MachinePerformanceData
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected String id;
    @XmlElement(required = true)
    protected String uri;
    protected boolean operationalstatus;
    @XmlElement(required = false)
    protected String statusmessage;
    @XmlElement(name = "NetworkAdapterPerformanceData")
    protected List<NetworkAdapterPerformanceData> networkAdapterPerformanceData;
    protected List<DriveInformation> driveInformation;
    @XmlElement(required = false, type = Long.class, nillable = true)
    protected Long bytesusedMemory;
    @XmlElement(required = false, type = Double.class, nillable = true)
    protected Double percentusedCPU;
    @XmlElement(required = false, type = Long.class, nillable = true)
    protected Long numberofActiveThreads;
    @XmlElement(required = false)
    @XmlSchemaType(name = "dateTime")
    protected Calendar timestamp;
    @XmlElement(required = false)
    @XmlSchemaType(name = "dateTime")
    protected Calendar startedAt;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
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

    /**
     * Gets the value of the operationalstatus property.
     * 
     */
    public boolean isOperationalstatus() {
        return operationalstatus;
    }

    /**
     * Sets the value of the operationalstatus property.
     * 
     */
    public void setOperationalstatus(boolean value) {
        this.operationalstatus = value;
    }

    /**
     * Gets the value of the statusmessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusmessage() {
        return statusmessage;
    }

    /**
     * Sets the value of the statusmessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusmessage(String value) {
        this.statusmessage = value;
    }

    /**
     * Gets the value of the networkAdapterPerformanceData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the networkAdapterPerformanceData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNetworkAdapterPerformanceData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NetworkAdapterPerformanceData }
     * 
     * 
     */
    public List<NetworkAdapterPerformanceData> getNetworkAdapterPerformanceData() {
        if (networkAdapterPerformanceData == null) {
            networkAdapterPerformanceData = new ArrayList<NetworkAdapterPerformanceData>();
        }
        return this.networkAdapterPerformanceData;
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

    /**
     * Gets the value of the bytesusedMemory property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getBytesusedMemory() {
        return bytesusedMemory;
    }

    /**
     * Sets the value of the bytesusedMemory property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setBytesusedMemory(Long value) {
        this.bytesusedMemory = value;
    }

    /**
     * Gets the value of the percentusedCPU property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getPercentusedCPU() {
        return percentusedCPU;
    }

    /**
     * Sets the value of the percentusedCPU property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setPercentusedCPU(Double value) {
        this.percentusedCPU = value;
    }

    /**
     * Gets the value of the numberofActiveThreads property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getNumberofActiveThreads() {
        return numberofActiveThreads;
    }

    /**
     * Sets the value of the numberofActiveThreads property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setNumberofActiveThreads(Long value) {
        this.numberofActiveThreads = value;
    }

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link Calendar }
     *     
     */
    public Calendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link Calendar }
     *     
     */
    public void setTimestamp(Calendar value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the startedAt property.
     * 
     * @return
     *     possible object is
     *     {@link Calendar }
     *     
     */
    public Calendar getStartedAt() {
        return startedAt;
    }

    /**
     * Sets the value of the startedAt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Calendar }
     *     
     */
    public void setStartedAt(Calendar value) {
        this.startedAt = value;
    }

}
