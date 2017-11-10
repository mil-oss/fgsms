
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
 *         &lt;element name="GetAvailableAlertRegistrationsResponseMsg" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}GetAvailableAlertRegistrationsResponseMsg"/>
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
    "getAvailableAlertRegistrationsResponseMsg"
})
@XmlRootElement(name = "GetAvailableAlertRegistrationsResponse")
public class GetAvailableAlertRegistrationsResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetAvailableAlertRegistrationsResponseMsg", required = true, nillable = true)
    protected GetAvailableAlertRegistrationsResponseMsg getAvailableAlertRegistrationsResponseMsg;

    /**
     * Gets the value of the getAvailableAlertRegistrationsResponseMsg property.
     * 
     * @return
     *     possible object is
     *     {@link GetAvailableAlertRegistrationsResponseMsg }
     *     
     */
    public GetAvailableAlertRegistrationsResponseMsg getGetAvailableAlertRegistrationsResponseMsg() {
        return getAvailableAlertRegistrationsResponseMsg;
    }

    /**
     * Sets the value of the getAvailableAlertRegistrationsResponseMsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetAvailableAlertRegistrationsResponseMsg }
     *     
     */
    public void setGetAvailableAlertRegistrationsResponseMsg(GetAvailableAlertRegistrationsResponseMsg value) {
        this.getAvailableAlertRegistrationsResponseMsg = value;
    }

    public boolean isSetGetAvailableAlertRegistrationsResponseMsg() {
        return (this.getAvailableAlertRegistrationsResponseMsg!= null);
    }

}
