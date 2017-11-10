
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * 
 *                 returns a list of service policies that are configured for uddi data publication
 *                 Note: Only information pretaining to uddi publication is returned,
 *                 other data such as SLA or UserIdentification policies are not returned.
 *             
 * 
 * <p>Java class for GetUDDIDataPublishServicePoliciesResponseMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetUDDIDataPublishServicePoliciesResponseMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="policies" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}ArrayOfServicePolicy"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetUDDIDataPublishServicePoliciesResponseMsg", propOrder = {
    "classification",
    "policies"
})
public class GetUDDIDataPublishServicePoliciesResponseMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(required = true, nillable = true)
    protected ArrayOfServicePolicy policies;

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
     * Gets the value of the policies property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfServicePolicy }
     *     
     */
    public ArrayOfServicePolicy getPolicies() {
        return policies;
    }

    /**
     * Sets the value of the policies property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfServicePolicy }
     *     
     */
    public void setPolicies(ArrayOfServicePolicy value) {
        this.policies = value;
    }

    public boolean isSetPolicies() {
        return (this.policies!= null);
    }

}
