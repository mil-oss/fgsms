
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
 *         &lt;element name="GetAlertRegistrationsResponseMsg" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}GetAlertRegistrationsResponseMsg"/>
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
    "getAlertRegistrationsResponseMsg"
})
@XmlRootElement(name = "GetAlertRegistrationsResponse")
public class GetAlertRegistrationsResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "GetAlertRegistrationsResponseMsg", required = true, nillable = true)
    protected GetAlertRegistrationsResponseMsg getAlertRegistrationsResponseMsg;

    /**
     * Gets the value of the getAlertRegistrationsResponseMsg property.
     * 
     * @return
     *     possible object is
     *     {@link GetAlertRegistrationsResponseMsg }
     *     
     */
    public GetAlertRegistrationsResponseMsg getGetAlertRegistrationsResponseMsg() {
        return getAlertRegistrationsResponseMsg;
    }

    /**
     * Sets the value of the getAlertRegistrationsResponseMsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetAlertRegistrationsResponseMsg }
     *     
     */
    public void setGetAlertRegistrationsResponseMsg(GetAlertRegistrationsResponseMsg value) {
        this.getAlertRegistrationsResponseMsg = value;
    }

    public boolean isSetGetAlertRegistrationsResponseMsg() {
        return (this.getAlertRegistrationsResponseMsg!= null);
    }

}
