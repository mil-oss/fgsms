
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
 * <p>Java class for SLARuleGeneric complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SLARuleGeneric">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}RuleBaseType">
 *       &lt;sequence>
 *         &lt;element name="ProcessAt" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}RunAtLocation"/>
 *         &lt;element name="ClassName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="parameter_name_value" type="{urn:org:miloss:fgsms:services:interfaces:common}NameValuePair" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SLARuleGeneric", propOrder = {
    "processAt",
    "className",
    "parameterNameValue"
})
public class SLARuleGeneric
    extends RuleBaseType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "ProcessAt", required = true, nillable = true)
    protected RunAtLocation processAt;
    @XmlElement(name = "ClassName", required = true)
    protected String className;
    @XmlElement(name = "parameter_name_value", required=false)
    protected List<NameValuePair> parameterNameValue;

    /**
     * Gets the value of the processAt property.
     * 
     * @return
     *     possible object is
     *     {@link RunAtLocation }
     *     
     */
    public RunAtLocation getProcessAt() {
        return processAt;
    }

    /**
     * Sets the value of the processAt property.
     * 
     * @param value
     *     allowed object is
     *     {@link RunAtLocation }
     *     
     */
    public void setProcessAt(RunAtLocation value) {
        this.processAt = value;
    }

    public boolean isSetProcessAt() {
        return (this.processAt!= null);
    }

    /**
     * Gets the value of the className property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the value of the className property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassName(String value) {
        this.className = value;
    }

    public boolean isSetClassName() {
        return (this.className!= null);
    }

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

}
