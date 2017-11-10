
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * This represents a policy for a specifc machine
 * 
 * Extends the service policy base type
 * 
 * <p>Java class for MachinePolicy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MachinePolicy">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}ServicePolicy">
 *       &lt;sequence>
 *         &lt;element name="RecordCPUusage" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="RecordMemoryUsage" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="RecordNetworkUsage" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="RecordDiskUsagePartitionNames" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="RecordDiskSpace" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MachinePolicy", propOrder = {
    "recordCPUusage",
    "recordMemoryUsage",
    "recordNetworkUsage",
    "recordDiskUsagePartitionNames",
    "recordDiskSpace"
})
@XmlRootElement
public class MachinePolicy
    extends ServicePolicy
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "RecordCPUusage")
    protected boolean recordCPUusage;
    @XmlElement(name = "RecordMemoryUsage")
    protected boolean recordMemoryUsage;
    @XmlElement(name = "RecordNetworkUsage")
    protected List<String> recordNetworkUsage;
    @XmlElement(name = "RecordDiskUsagePartitionNames")
    protected List<String> recordDiskUsagePartitionNames;
    @XmlElement(name = "RecordDiskSpace")
    protected List<String> recordDiskSpace;

    /**
     * Gets the value of the recordCPUusage property.
     * 
     */
    public boolean isRecordCPUusage() {
        return recordCPUusage;
    }

    /**
     * Sets the value of the recordCPUusage property.
     * 
     */
    public void setRecordCPUusage(boolean value) {
        this.recordCPUusage = value;
    }

    public boolean isSetRecordCPUusage() {
        return true;
    }

    /**
     * Gets the value of the recordMemoryUsage property.
     * 
     */
    public boolean isRecordMemoryUsage() {
        return recordMemoryUsage;
    }

    /**
     * Sets the value of the recordMemoryUsage property.
     * 
     */
    public void setRecordMemoryUsage(boolean value) {
        this.recordMemoryUsage = value;
    }

    public boolean isSetRecordMemoryUsage() {
        return true;
    }

    /**
     * Gets the value of the recordNetworkUsage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the recordNetworkUsage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRecordNetworkUsage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRecordNetworkUsage() {
        if (recordNetworkUsage == null) {
            recordNetworkUsage = new ArrayList<String>();
        }
        return this.recordNetworkUsage;
    }

    public boolean isSetRecordNetworkUsage() {
        return ((this.recordNetworkUsage!= null)&&(!this.recordNetworkUsage.isEmpty()));
    }

    public void unsetRecordNetworkUsage() {
        this.recordNetworkUsage = null;
    }

    /**
     * Gets the value of the recordDiskUsagePartitionNames property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the recordDiskUsagePartitionNames property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRecordDiskUsagePartitionNames().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRecordDiskUsagePartitionNames() {
        if (recordDiskUsagePartitionNames == null) {
            recordDiskUsagePartitionNames = new ArrayList<String>();
        }
        return this.recordDiskUsagePartitionNames;
    }

    public boolean isSetRecordDiskUsagePartitionNames() {
        return ((this.recordDiskUsagePartitionNames!= null)&&(!this.recordDiskUsagePartitionNames.isEmpty()));
    }

    public void unsetRecordDiskUsagePartitionNames() {
        this.recordDiskUsagePartitionNames = null;
    }

    /**
     * Gets the value of the recordDiskSpace property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the recordDiskSpace property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRecordDiskSpace().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRecordDiskSpace() {
        if (recordDiskSpace == null) {
            recordDiskSpace = new ArrayList<String>();
        }
        return this.recordDiskSpace;
    }

    public boolean isSetRecordDiskSpace() {
        return ((this.recordDiskSpace!= null)&&(!this.recordDiskSpace.isEmpty()));
    }

    public void unsetRecordDiskSpace() {
        this.recordDiskSpace = null;
    }

}
