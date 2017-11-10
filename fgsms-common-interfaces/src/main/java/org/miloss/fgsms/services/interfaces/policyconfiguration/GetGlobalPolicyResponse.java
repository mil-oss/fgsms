
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
 *         &lt;element name="GetGlobalPolicyResult" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}GetGlobalPolicyResponseMsg"/>
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
    "getGlobalPolicyResult"
})
@XmlRootElement(name = "GetGlobalPolicyResponse")
public class GetGlobalPolicyResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetGlobalPolicyResult", required = true, nillable = true)
    protected GetGlobalPolicyResponseMsg getGlobalPolicyResult;

    /**
     * Gets the value of the getGlobalPolicyResult property.
     * 
     * @return
     *     possible object is
     *     {@link GetGlobalPolicyResponseMsg }
     *     
     */
    public GetGlobalPolicyResponseMsg getGetGlobalPolicyResult() {
        return getGlobalPolicyResult;
    }

    /**
     * Sets the value of the getGlobalPolicyResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetGlobalPolicyResponseMsg }
     *     
     */
    public void setGetGlobalPolicyResult(GetGlobalPolicyResponseMsg value) {
        this.getGlobalPolicyResult = value;
    }

    public boolean isSetGetGlobalPolicyResult() {
        return (this.getGlobalPolicyResult!= null);
    }

}
