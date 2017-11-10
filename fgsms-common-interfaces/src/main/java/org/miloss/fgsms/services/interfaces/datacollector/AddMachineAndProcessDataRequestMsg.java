
package org.miloss.fgsms.services.interfaces.datacollector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.MachinePerformanceData;
import org.miloss.fgsms.services.interfaces.common.NameValuePairSet;
import org.miloss.fgsms.services.interfaces.common.ProcessPerformanceData;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * request message
 * 
 * <p>Java class for AddMachineAndProcessDataRequestMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AddMachineAndProcessDataRequestMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="hostname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="agentType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="domainname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MachineData" type="{urn:org:miloss:fgsms:services:interfaces:common}MachinePerformanceData"/>
 *         &lt;element name="ProcessData" type="{urn:org:miloss:fgsms:services:interfaces:common}ProcessPerformanceData" maxOccurs="unbounded"/>
 *         &lt;element name="SensorData" type="{urn:org:miloss:fgsms:services:interfaces:common}NameValuePairSet"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddMachineAndProcessDataRequestMsg", propOrder = {
    "classification",
    "hostname",
    "agentType",
    "domainname",
    "machineData",
    "processData",
    "sensorData"
})
public class AddMachineAndProcessDataRequestMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(required = true, nillable = true)
    protected String hostname;
    @XmlElement(required = true, nillable = true)
    protected String agentType;
    @XmlElement(required = true, nillable = true)
    protected String domainname;
    @XmlElement(name = "MachineData", required = false, nillable = true)
    protected MachinePerformanceData machineData;
    @XmlElement(name = "ProcessData", required = false, nillable = true)
    protected List<ProcessPerformanceData> processData;
    @XmlElement(name = "SensorData", required = false, nillable = true)
    protected NameValuePairSet sensorData;

    /**
     * Gets the value of the classification property.
     * 
     * @return
     *     possible object is
     *     {@link SecurityWrapper }
     *     
     */
    public SecurityWrapper getClassification() {
        return classification;
    }

    /**
     * Sets the value of the classification property.
     * 
     * @param value
     *     allowed object is
     *     {@link SecurityWrapper }
     *     
     */
    public void setClassification(SecurityWrapper value) {
        this.classification = value;
    }

    public boolean isSetClassification() {
        return (this.classification!= null);
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
     * Gets the value of the agentType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgentType() {
        return agentType;
    }

    /**
     * Sets the value of the agentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgentType(String value) {
        this.agentType = value;
    }

    public boolean isSetAgentType() {
        return (this.agentType!= null);
    }

    /**
     * Gets the value of the domainname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomainname() {
        return domainname;
    }

    /**
     * Sets the value of the domainname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomainname(String value) {
        this.domainname = value;
    }

    public boolean isSetDomainname() {
        return (this.domainname!= null);
    }

    /**
     * Gets the value of the machineData property.
     * 
     * @return
     *     possible object is
     *     {@link MachinePerformanceData }
     *     
     */
    public MachinePerformanceData getMachineData() {
        return machineData;
    }

    /**
     * Sets the value of the machineData property.
     * 
     * @param value
     *     allowed object is
     *     {@link MachinePerformanceData }
     *     
     */
    public void setMachineData(MachinePerformanceData value) {
        this.machineData = value;
    }

    public boolean isSetMachineData() {
        return (this.machineData!= null);
    }

    /**
     * Gets the value of the processData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the processData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcessData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProcessPerformanceData }
     * 
     * 
     */
    public List<ProcessPerformanceData> getProcessData() {
        if (processData == null) {
            processData = new ArrayList<ProcessPerformanceData>();
        }
        return this.processData;
    }

    public boolean isSetProcessData() {
        return ((this.processData!= null)&&(!this.processData.isEmpty()));
    }

    public void unsetProcessData() {
        this.processData = null;
    }

    /**
     * Gets the value of the sensorData property.
     * 
     * @return
     *     possible object is
     *     {@link NameValuePairSet }
     *     
     */
    public NameValuePairSet getSensorData() {
        return sensorData;
    }

    /**
     * Sets the value of the sensorData property.
     * 
     * @param value
     *     allowed object is
     *     {@link NameValuePairSet }
     *     
     */
    public void setSensorData(NameValuePairSet value) {
        this.sensorData = value;
    }

    public boolean isSetSensorData() {
        return (this.sensorData!= null);
    }

}
