
package org.miloss.fgsms.services.interfaces.policyconfiguration;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="request" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}SetServicePolicyRequestMsg"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "request"
})
@XmlRootElement(name = "SetServicePolicy")
public class SetServicePolicy
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true, nillable = true)
    protected SetServicePolicyRequestMsg request;

    /**
     * Gets the value of the request property.
     * 
     * @return
     *     possible object is
     *     {@link SetServicePolicyRequestMsg }
     *     
     */
    public SetServicePolicyRequestMsg getRequest() {
        return request;
    }

    /**
     * Sets the value of the request property.
     * 
     * @param value
     *     allowed object is
     *     {@link SetServicePolicyRequestMsg }
     *     
     */
    public void setRequest(SetServicePolicyRequestMsg value) {
        this.request = value;
    }

    public boolean isSetRequest() {
        return (this.request!= null);
    }

}