
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.AgentAction;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * 
 *         updates an action, requires agent rights
 *            
 * 
 * <p>Java class for UpdateAgentActionRequestMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateAgentActionRequestMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="AgentAction" type="{urn:org:miloss:fgsms:services:interfaces:common}AgentAction"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateAgentActionRequestMsg", propOrder = {
    "classification",
    "agentAction"
})
public class UpdateAgentActionRequestMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(name = "AgentAction", required = true)
    protected AgentAction agentAction;

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
     * Gets the value of the agentAction property.
     * 
     * @return
     *     possible object is
     *     {@link AgentAction }
     *     
     */
    public AgentAction getAgentAction() {
        return agentAction;
    }

    /**
     * Sets the value of the agentAction property.
     * 
     * @param value
     *     allowed object is
     *     {@link AgentAction }
     *     
     */
    public void setAgentAction(AgentAction value) {
        this.agentAction = value;
    }

    public boolean isSetAgentAction() {
        return (this.agentAction!= null);
    }

}
