
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;


/**
 * 
 *             
 * 
 * <p>Java class for SLAAction complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SLAAction">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="parameter_name_value" type="{urn:org:miloss:fgsms:services:interfaces:common}NameValuePair" maxOccurs="unbounded"/>
 *         &lt;element name="implementing_class_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SLAAction", propOrder = {
    "parameterNameValue",
    "implementingClassName"
})
public class SLAAction
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "parameter_name_value", required = false)
    protected List<NameValuePair> parameterNameValue;
    @XmlElement(name = "implementing_class_name", required = true)
    protected String implementingClassName;

    /**
     * Gets the value of the parameterNameValue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parameterNameValue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParameterNameValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NameValuePair }
     * 
     * 
     */
    public List<NameValuePair> getParameterNameValue() {
        if (parameterNameValue == null) {
            parameterNameValue = new ArrayList<NameValuePair>();
        }
        return this.parameterNameValue;
    }

    public boolean isSetParameterNameValue() {
        return ((this.parameterNameValue!= null)&&(!this.parameterNameValue.isEmpty()));
    }

    public void unsetParameterNameValue() {
        this.parameterNameValue = null;
    }

    /**
     * Gets the value of the implementingClassName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImplementingClassName() {
        return implementingClassName;
    }

    /**
     * Sets the value of the implementingClassName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImplementingClassName(String value) {
        this.implementingClassName = value;
    }

    public boolean isSetImplementingClassName() {
        return (this.implementingClassName!= null);
    }

}
