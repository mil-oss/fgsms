
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
 *         &lt;element name="GetServicePolicyResult" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}ServicePolicyResponseMsg"/>
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
    "getServicePolicyResult"
})
@XmlRootElement(name = "GetServicePolicyResponse")
public class GetServicePolicyResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetServicePolicyResult", required = true)
    protected ServicePolicyResponseMsg getServicePolicyResult;

    /**
     * Gets the value of the getServicePolicyResult property.
     * 
     * @return
     *     possible object is
     *     {@link ServicePolicyResponseMsg }
     *     
     */
    public ServicePolicyResponseMsg getGetServicePolicyResult() {
        return getServicePolicyResult;
    }

    /**
     * Sets the value of the getServicePolicyResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServicePolicyResponseMsg }
     *     
     */
    public void setGetServicePolicyResult(ServicePolicyResponseMsg value) {
        this.getServicePolicyResult = value;
    }

    public boolean isSetGetServicePolicyResult() {
        return (this.getServicePolicyResult!= null);
    }

}
