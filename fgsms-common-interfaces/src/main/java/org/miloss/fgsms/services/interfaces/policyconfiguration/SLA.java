
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *             Service Level Aggrements
 *            
 * 
 * <p>Java class for SLA complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SLA">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="guid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Action" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}ArrayOfSLAActionBaseType"/>
 *         &lt;element name="Rule" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}RuleBaseType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SLA", propOrder = {
    "guid",
    "action",
    "rule"
})
public class SLA
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected String guid;
    @XmlElement(name = "Action", required = true, nillable = true)
    protected ArrayOfSLAActionBaseType action;
    @XmlElement(name = "Rule", required = true, nillable = true)
    protected RuleBaseType rule;

    /**
     * Gets the value of the guid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuid() {
        return guid;
    }

    /**
     * Sets the value of the guid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuid(String value) {
        this.guid = value;
    }

    public boolean isSetGuid() {
        return (this.guid!= null);
    }

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSLAActionBaseType }
     *     
     */
    public ArrayOfSLAActionBaseType getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSLAActionBaseType }
     *     
     */
    public void setAction(ArrayOfSLAActionBaseType value) {
        this.action = value;
    }

    public boolean isSetAction() {
        return (this.action!= null);
    }

    /**
     * Gets the value of the rule property.
     * 
     * @return
     *     possible object is
     *     {@link RuleBaseType }
     *     
     */
    public RuleBaseType getRule() {
        return rule;
    }

    /**
     * Sets the value of the rule property.
     * 
     * @param value
     *     allowed object is
     *     {@link RuleBaseType }
     *     
     */
    public void setRule(RuleBaseType value) {
        this.rule = value;
    }

    public boolean isSetRule() {
        return (this.rule!= null);
    }

}
