
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * 
 *             contains the current list of 
 *            
 * 
 * <p>Java class for SetProcessListByMachineResponseMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetProcessListByMachineResponseMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="ReportingFrequency" type="{http://www.w3.org/2001/XMLSchema}duration"/>
 *         &lt;element name="MachinePolicy" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}MachinePolicy"/>
 *         &lt;element name="ProcessPolicy" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}ProcessPolicy" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetProcessListByMachineResponseMsg", propOrder = {
    "classification",
    "reportingFrequency",
    "machinePolicy",
    "processPolicy"
})
public class SetProcessListByMachineResponseMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(name = "ReportingFrequency", required = true)
    protected Duration reportingFrequency;
    @XmlElement(name = "MachinePolicy", required = true)
    protected MachinePolicy machinePolicy;
    @XmlElement(name = "ProcessPolicy")
    protected List<ProcessPolicy> processPolicy;

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
     * Gets the value of the reportingFrequency property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getReportingFrequency() {
        return reportingFrequency;
    }

    /**
     * Sets the value of the reportingFrequency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setReportingFrequency(Duration value) {
        this.reportingFrequency = value;
    }

    public boolean isSetReportingFrequency() {
        return (this.reportingFrequency!= null);
    }

    /**
     * Gets the value of the machinePolicy property.
     * 
     * @return
     *     possible object is
     *     {@link MachinePolicy }
     *     
     */
    public MachinePolicy getMachinePolicy() {
        return machinePolicy;
    }

    /**
     * Sets the value of the machinePolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link MachinePolicy }
     *     
     */
    public void setMachinePolicy(MachinePolicy value) {
        this.machinePolicy = value;
    }

    public boolean isSetMachinePolicy() {
        return (this.machinePolicy!= null);
    }

    /**
     * Gets the value of the processPolicy property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the processPolicy property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcessPolicy().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProcessPolicy }
     * 
     * 
     */
    public List<ProcessPolicy> getProcessPolicy() {
        if (processPolicy == null) {
            processPolicy = new ArrayList<ProcessPolicy>();
        }
        return this.processPolicy;
    }

    public boolean isSetProcessPolicy() {
        return ((this.processPolicy!= null)&&(!this.processPolicy.isEmpty()));
    }

    public void unsetProcessPolicy() {
        this.processPolicy = null;
    }

}
