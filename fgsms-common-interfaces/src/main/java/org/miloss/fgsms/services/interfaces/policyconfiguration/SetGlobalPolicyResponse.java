
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
 *         &lt;element name="SetGlobalPolicyResult" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}SetGlobalPolicyResponseMsg"/>
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
    "setGlobalPolicyResult"
})
@XmlRootElement(name = "SetGlobalPolicyResponse")
public class SetGlobalPolicyResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "SetGlobalPolicyResult", required = true, nillable = true)
    protected SetGlobalPolicyResponseMsg setGlobalPolicyResult;

    /**
     * Gets the value of the setGlobalPolicyResult property.
     * 
     * @return
     *     possible object is
     *     {@link SetGlobalPolicyResponseMsg }
     *     
     */
    public SetGlobalPolicyResponseMsg getSetGlobalPolicyResult() {
        return setGlobalPolicyResult;
    }

    /**
     * Sets the value of the setGlobalPolicyResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SetGlobalPolicyResponseMsg }
     *     
     */
    public void setSetGlobalPolicyResult(SetGlobalPolicyResponseMsg value) {
        this.setGlobalPolicyResult = value;
    }

    public boolean isSetSetGlobalPolicyResult() {
        return (this.setGlobalPolicyResult!= null);
    }

}
