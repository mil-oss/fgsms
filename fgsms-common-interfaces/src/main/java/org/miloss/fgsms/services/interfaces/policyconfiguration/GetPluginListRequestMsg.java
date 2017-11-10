
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * 
 *         gets the plugin list
 *            
 * 
 * <p>Java class for GetPluginListRequestMsg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetPluginListRequestMsg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="plugintype" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="optionalPolicyTypeFilter" type="{urn:org:miloss:fgsms:services:interfaces:common}policyType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetPluginListRequestMsg", propOrder = {
    "classification",
    "plugintype",
    "optionalPolicyTypeFilter"
})
public class GetPluginListRequestMsg
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(required = true, nillable = true)
    protected String plugintype;
    @XmlElement(required = true, nillable = true)
    protected PolicyType optionalPolicyTypeFilter;

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
     * Gets the value of the plugintype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlugintype() {
        return plugintype;
    }

    /**
     * Sets the value of the plugintype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlugintype(String value) {
        this.plugintype = value;
    }

    public boolean isSetPlugintype() {
        return (this.plugintype!= null);
    }

    /**
     * Gets the value of the optionalPolicyTypeFilter property.
     * 
     * @return
     *     possible object is
     *     {@link PolicyType }
     *     
     */
    public PolicyType getOptionalPolicyTypeFilter() {
        return optionalPolicyTypeFilter;
    }

    /**
     * Sets the value of the optionalPolicyTypeFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyType }
     *     
     */
    public void setOptionalPolicyTypeFilter(PolicyType value) {
        this.optionalPolicyTypeFilter = value;
    }

    public boolean isSetOptionalPolicyTypeFilter() {
        return (this.optionalPolicyTypeFilter!= null);
    }

}
