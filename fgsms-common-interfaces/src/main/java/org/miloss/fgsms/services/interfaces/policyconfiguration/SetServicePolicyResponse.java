
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
 *         &lt;element name="SetServicePolicyResult" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}SetServicePolicyResponseMsg"/>
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
    "setServicePolicyResult"
})
@XmlRootElement(name = "SetServicePolicyResponse")
public class SetServicePolicyResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "SetServicePolicyResult", required = true, nillable = true)
    protected SetServicePolicyResponseMsg setServicePolicyResult;

    /**
     * Gets the value of the setServicePolicyResult property.
     * 
     * @return
     *     possible object is
     *     {@link SetServicePolicyResponseMsg }
     *     
     */
    public SetServicePolicyResponseMsg getSetServicePolicyResult() {
        return setServicePolicyResult;
    }

    /**
     * Sets the value of the setServicePolicyResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SetServicePolicyResponseMsg }
     *     
     */
    public void setSetServicePolicyResult(SetServicePolicyResponseMsg value) {
        this.setServicePolicyResult = value;
    }

    public boolean isSetSetServicePolicyResult() {
        return (this.setServicePolicyResult!= null);
    }

}
