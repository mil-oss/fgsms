
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.ProcessPerformanceData;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * response message
 * 
 * <p>Java class for GetProcessPerformanceLogsByRangeResponseMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetProcessPerformanceLogsByRangeResponseMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="ProcessData" type="{urn:org:miloss:fgsms:services:interfaces:common}ProcessPerformanceData" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="InstalledMemory" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetProcessPerformanceLogsByRangeResponseMsg", propOrder = {
    "classification",
    "processData",
    "installedMemory"
})
public class GetProcessPerformanceLogsByRangeResponseMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(name = "ProcessData")
    protected List<ProcessPerformanceData> processData;
    @XmlElement(name = "InstalledMemory")
    protected long installedMemory;

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
     * Gets the value of the installedMemory property.
     * 
     */
    public long getInstalledMemory() {
        return installedMemory;
    }

    /**
     * Sets the value of the installedMemory property.
     * 
     */
    public void setInstalledMemory(long value) {
        this.installedMemory = value;
    }

    public boolean isSetInstalledMemory() {
        return true;
    }

}
