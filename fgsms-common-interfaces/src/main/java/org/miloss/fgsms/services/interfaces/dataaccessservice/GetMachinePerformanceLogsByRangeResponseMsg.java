
package org.miloss.fgsms.services.interfaces.dataaccessservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.MachinePerformanceData;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * response message
 * 
 * <p>Java class for GetMachinePerformanceLogsByRangeResponseMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetMachinePerformanceLogsByRangeResponseMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="MachinePerformanceData" type="{urn:org:miloss:fgsms:services:interfaces:common}MachinePerformanceData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetMachinePerformanceLogsByRangeResponseMsg", propOrder = {
    "classification",
    "machinePerformanceData"
})
public class GetMachinePerformanceLogsByRangeResponseMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(name = "MachinePerformanceData")
    protected List<MachinePerformanceData> machinePerformanceData;

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
     * Gets the value of the machinePerformanceData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the machinePerformanceData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMachinePerformanceData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MachinePerformanceData }
     * 
     * 
     */
    public List<MachinePerformanceData> getMachinePerformanceData() {
        if (machinePerformanceData == null) {
            machinePerformanceData = new ArrayList<MachinePerformanceData>();
        }
        return this.machinePerformanceData;
    }

    public boolean isSetMachinePerformanceData() {
        return ((this.machinePerformanceData!= null)&&(!this.machinePerformanceData.isEmpty()));
    }

    public void unsetMachinePerformanceData() {
        this.machinePerformanceData = null;
    }

}
