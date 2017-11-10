
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * joining expressions together, applies to all services
 * 			
 * 
 * SLA Rule base type
 * 
 * <p>Java class for AndOrNot complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AndOrNot">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}RuleBaseType">
 *       &lt;sequence>
 *         &lt;element name="flag" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}JoiningType"/>
 *         &lt;element name="LHS" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}RuleBaseType"/>
 *         &lt;element name="RHS" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}RuleBaseType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AndOrNot", propOrder = {
    "flag",
    "lhs",
    "rhs"
})
public class AndOrNot
    extends RuleBaseType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected JoiningType flag;
    @XmlElement(name = "LHS", required = true)
    protected RuleBaseType lhs;
    @XmlElement(name = "RHS", required = true)
    protected RuleBaseType rhs;

    /**
     * Gets the value of the flag property.
     * 
     * @return
     *     possible object is
     *     {@link JoiningType }
     *     
     */
    public JoiningType getFlag() {
        return flag;
    }

    /**
     * Sets the value of the flag property.
     * 
     * @param value
     *     allowed object is
     *     {@link JoiningType }
     *     
     */
    public void setFlag(JoiningType value) {
        this.flag = value;
    }

    public boolean isSetFlag() {
        return (this.flag!= null);
    }

    /**
     * Gets the value of the lhs property.
     * 
     * @return
     *     possible object is
     *     {@link RuleBaseType }
     *     
     */
    public RuleBaseType getLHS() {
        return lhs;
    }

    /**
     * Sets the value of the lhs property.
     * 
     * @param value
     *     allowed object is
     *     {@link RuleBaseType }
     *     
     */
    public void setLHS(RuleBaseType value) {
        this.lhs = value;
    }

    public boolean isSetLHS() {
        return (this.lhs!= null);
    }

    /**
     * Gets the value of the rhs property.
     * 
     * @return
     *     possible object is
     *     {@link RuleBaseType }
     *     
     */
    public RuleBaseType getRHS() {
        return rhs;
    }

    /**
     * Sets the value of the rhs property.
     * 
     * @param value
     *     allowed object is
     *     {@link RuleBaseType }
     *     
     */
    public void setRHS(RuleBaseType value) {
        this.rhs = value;
    }

    public boolean isSetRHS() {
        return (this.rhs!= null);
    }

}
