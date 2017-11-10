
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
 *         &lt;element name="DeleteServicePolicyResult" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}DeleteServicePolicyResponseMsg"/>
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
    "deleteServicePolicyResult"
})
@XmlRootElement(name = "DeleteServicePolicyResponse")
public class DeleteServicePolicyResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "DeleteServicePolicyResult", required = true, nillable = true)
    protected DeleteServicePolicyResponseMsg deleteServicePolicyResult;

    /**
     * Gets the value of the deleteServicePolicyResult property.
     * 
     * @return
     *     possible object is
     *     {@link DeleteServicePolicyResponseMsg }
     *     
     */
    public DeleteServicePolicyResponseMsg getDeleteServicePolicyResult() {
        return deleteServicePolicyResult;
    }

    /**
     * Sets the value of the deleteServicePolicyResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeleteServicePolicyResponseMsg }
     *     
     */
    public void setDeleteServicePolicyResult(DeleteServicePolicyResponseMsg value) {
        this.deleteServicePolicyResult = value;
    }

    public boolean isSetDeleteServicePolicyResult() {
        return (this.deleteServicePolicyResult!= null);
    }

}
