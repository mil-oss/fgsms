
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * 
 *          sets a list of processes that have been observed
 * 			requires agent
 *            
 * 
 * <p>Java class for SetProcessListByMachineRequestMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetProcessListByMachineRequestMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="agentType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="MachineInformation" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}MachineInformation"/>
 *         &lt;element name="services" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
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
@XmlType(name = "SetProcessListByMachineRequestMsg", propOrder = {
    "classification",
    "agentType",
    "machineInformation",
    "services"
})
public class SetProcessListByMachineRequestMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(required = true, nillable = true)
    protected String agentType;
    @XmlElement(name = "MachineInformation", required = true)
    protected MachineInformation machineInformation;
    @XmlElement(required = false, nillable = true)
    protected List<String> services;

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
     * Gets the value of the services property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the services property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getServices().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getServices() {
        if (services == null) {
            services = new ArrayList<String>();
        }
        return this.services;
    }

    public boolean isSetServices() {
        return ((this.services!= null)&&(!this.services.isEmpty()));
    }

    public void unsetServices() {
        this.services = null;
    }

}
