
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;


/**
 * 
 *                     get formatted html
 *                 
 * 
 * <p>Java class for GetPluginHtmlFormattedDisplayRequestMsgWrapper complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetPluginHtmlFormattedDisplayRequestMsgWrapper">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classification" type="{urn:org:miloss:fgsms:services:interfaces:common}SecurityWrapper"/>
 *         &lt;element name="plugintype" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="classname" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="parameters" type="{urn:org:miloss:fgsms:services:interfaces:common}NameValuePair" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetPluginHtmlFormattedDisplayRequestMsgWrapper", propOrder = {
    "classification",
    "plugintype",
    "classname",
    "parameters"
})
public class GetPluginHtmlFormattedDisplayRequestMsgWrapper
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SecurityWrapper classification;
    @XmlElement(required = true, nillable = true)
    protected String plugintype;
    @XmlElement(required = true, nillable = true)
    protected String classname;
    @XmlElement(required = true, nillable = true)
    protected List<NameValuePair> parameters;

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
     * Gets the value of the parameters property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parameters property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParameters().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NameValuePair }
     * 
     * 
     */
    public List<NameValuePair> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<NameValuePair>();
        }
        return this.parameters;
    }

    public boolean isSetParameters() {
        return ((this.parameters!= null)&&(!this.parameters.isEmpty()));
    }

    public void unsetParameters() {
        this.parameters = null;
    }

}
