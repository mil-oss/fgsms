
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.PolicyType;


/**
 * <p>Java class for plugin complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="plugin">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="displayname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="plugintype" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="policyTypes" type="{urn:org:miloss:fgsms:services:interfaces:common}policyType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "plugin", propOrder = {
    "classname",
    "displayname",
    "plugintype",
    "policyTypes"
})
public class Plugin
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected String classname;
    @XmlElement(required = true, nillable = true)
    protected String displayname;
    @XmlElement(required = true, nillable = true)
    protected String plugintype;
    @XmlElement(required = false, nillable = true)
    protected List<PolicyType> policyTypes;

    /**
     * Gets the value of the classname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassname() {
        return classname;
    }

    /**
     * Sets the value of the classname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassname(String value) {
        this.classname = value;
    }

    public boolean isSetClassname() {
        return (this.classname!= null);
    }

    /**
     * Gets the value of the displayname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayname() {
        return displayname;
    }

    /**
     * Sets the value of the displayname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayname(String value) {
        this.displayname = value;
    }

    public boolean isSetDisplayname() {
        return (this.displayname!= null);
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
     * Gets the value of the policyTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the policyTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPolicyTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PolicyType }
     * 
     * 
     */
    public List<PolicyType> getPolicyTypes() {
        if (policyTypes == null) {
            policyTypes = new ArrayList<PolicyType>();
        }
        return this.policyTypes;
    }

    public boolean isSetPolicyTypes() {
        return ((this.policyTypes!= null)&&(!this.policyTypes.isEmpty()));
    }

    public void unsetPolicyTypes() {
        this.policyTypes = null;
    }

}
