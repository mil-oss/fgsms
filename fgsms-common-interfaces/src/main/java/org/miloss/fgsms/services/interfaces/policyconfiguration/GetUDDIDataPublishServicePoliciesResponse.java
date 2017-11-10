
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
 *         &lt;element name="GetUDDIDataPublishServicePoliciesResult" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}GetUDDIDataPublishServicePoliciesResponseMsg"/>
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
    "getUDDIDataPublishServicePoliciesResult"
})
@XmlRootElement(name = "GetUDDIDataPublishServicePoliciesResponse")
public class GetUDDIDataPublishServicePoliciesResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetUDDIDataPublishServicePoliciesResult", required = true, nillable = true)
    protected GetUDDIDataPublishServicePoliciesResponseMsg getUDDIDataPublishServicePoliciesResult;

    /**
     * Gets the value of the getUDDIDataPublishServicePoliciesResult property.
     * 
     * @return
     *     possible object is
     *     {@link GetUDDIDataPublishServicePoliciesResponseMsg }
     *     
     */
    public GetUDDIDataPublishServicePoliciesResponseMsg getGetUDDIDataPublishServicePoliciesResult() {
        return getUDDIDataPublishServicePoliciesResult;
    }

    /**
     * Sets the value of the getUDDIDataPublishServicePoliciesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetUDDIDataPublishServicePoliciesResponseMsg }
     *     
     */
    public void setGetUDDIDataPublishServicePoliciesResult(GetUDDIDataPublishServicePoliciesResponseMsg value) {
        this.getUDDIDataPublishServicePoliciesResult = value;
    }

    public boolean isSetGetUDDIDataPublishServicePoliciesResult() {
        return (this.getUDDIDataPublishServicePoliciesResult!= null);
    }

}
