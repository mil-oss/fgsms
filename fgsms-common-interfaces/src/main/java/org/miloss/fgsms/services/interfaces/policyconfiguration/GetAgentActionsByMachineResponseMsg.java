
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.AgentAction;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * 
 *             list of processes
 *            
 * 
 * <p>Java class for GetAgentActionsByMachineResponseMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetAgentActionsByMachineResponseMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="AgentActions" type="{urn:org:miloss:fgsms:services:interfaces:common}AgentAction" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetAgentActionsByMachineResponseMsg", propOrder = {
    "classification",
    "agentActions"
})
public class GetAgentActionsByMachineResponseMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(name = "AgentActions", required = true)
    protected List<AgentAction> agentActions;

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
     * Gets the value of the agentActions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the agentActions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAgentActions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AgentAction }
     * 
     * 
     */
    public List<AgentAction> getAgentActions() {
        if (agentActions == null) {
            agentActions = new ArrayList<AgentAction>();
        }
        return this.agentActions;
    }

    public boolean isSetAgentActions() {
        return ((this.agentActions!= null)&&(!this.agentActions.isEmpty()));
    }

    public void unsetAgentActions() {
        this.agentActions = null;
    }

}
