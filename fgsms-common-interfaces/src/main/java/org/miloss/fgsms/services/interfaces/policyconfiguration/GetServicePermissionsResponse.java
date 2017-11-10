
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
 *         &lt;element name="GetServicePermissionsResult" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}GetServicePermissionsResponseMsg"/>
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
    "getServicePermissionsResult"
})
@XmlRootElement(name = "GetServicePermissionsResponse")
public class GetServicePermissionsResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetServicePermissionsResult", required = true, nillable = true)
    protected GetServicePermissionsResponseMsg getServicePermissionsResult;

    /**
     * Gets the value of the getServicePermissionsResult property.
     * 
     * @return
     *     possible object is
     *     {@link GetServicePermissionsResponseMsg }
     *     
     */
    public GetServicePermissionsResponseMsg getGetServicePermissionsResult() {
        return getServicePermissionsResult;
    }

    /**
     * Sets the value of the getServicePermissionsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetServicePermissionsResponseMsg }
     *     
     */
    public void setGetServicePermissionsResult(GetServicePermissionsResponseMsg value) {
        this.getServicePermissionsResult = value;
    }

    public boolean isSetGetServicePermissionsResult() {
        return (this.getServicePermissionsResult!= null);
    }

}
