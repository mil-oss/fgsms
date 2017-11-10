
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import java.util.Calendar;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * 
 *             list of processes
 *            
 * 
 * <p>Java class for GetProcessesListByMachineResponseMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetProcessesListByMachineResponseMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="ProcessName" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="MachineInformation" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}MachineInformation"/>
 *         &lt;element name="lastupdateat" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetProcessesListByMachineResponseMsg", propOrder = {
    "classification",
    "processName",
    "machineInformation",
    "lastupdateat"
})
public class GetProcessesListByMachineResponseMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(name = "ProcessName")
    protected List<String> processName;
    @XmlElement(name = "MachineInformation", required = true, nillable = true)
    protected MachineInformation machineInformation;
    @XmlElement(required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected Calendar lastupdateat;

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
     * Gets the value of the processName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the processName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcessName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getProcessName() {
        if (processName == null) {
            processName = new ArrayList<String>();
        }
        return this.processName;
    }

    public boolean isSetProcessName() {
        return ((this.processName!= null)&&(!this.processName.isEmpty()));
    }

    public void unsetProcessName() {
        this.processName = null;
    }

    /**
     * Gets the value of the machineInformation property.
     * 
     * @return
     *     possible object is
     *     {@link MachineInformation }
     *     
     */
    public MachineInformation getMachineInformation() {
        return machineInformation;
    }

    /**
     * Sets the value of the machineInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link MachineInformation }
     *     
     */
    public void setMachineInformation(MachineInformation value) {
        this.machineInformation = value;
    }

    public boolean isSetMachineInformation() {
        return (this.machineInformation!= null);
    }

    /**
     * Gets the value of the lastupdateat property.
     * 
     * @return
     *     possible object is
     *     {@link Calendar }
     *     
     */
    public Calendar getLastupdateat() {
        return lastupdateat;
    }

    /**
     * Sets the value of the lastupdateat property.
     * 
     * @param value
     *     allowed object is
     *     {@link Calendar }
     *     
     */
    public void setLastupdateat(Calendar value) {
        this.lastupdateat = value;
    }

    public boolean isSetLastupdateat() {
        return (this.lastupdateat!= null);
    }

}
