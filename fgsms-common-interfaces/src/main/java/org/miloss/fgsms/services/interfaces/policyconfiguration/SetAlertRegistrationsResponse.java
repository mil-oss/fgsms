
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
 *         &lt;element name="SetAlertRegistrationsResponseMsg" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}SetAlertRegistrationsResponseMsg"/>
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
    "setAlertRegistrationsResponseMsg"
})
@XmlRootElement(name = "SetAlertRegistrationsResponse")
public class SetAlertRegistrationsResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "SetAlertRegistrationsResponseMsg", required = true, nillable = true)
    protected SetAlertRegistrationsResponseMsg setAlertRegistrationsResponseMsg;

    /**
     * Gets the value of the setAlertRegistrationsResponseMsg property.
     * 
     * @return
     *     possible object is
     *     {@link SetAlertRegistrationsResponseMsg }
     *     
     */
    public SetAlertRegistrationsResponseMsg getSetAlertRegistrationsResponseMsg() {
        return setAlertRegistrationsResponseMsg;
    }

    /**
     * Sets the value of the setAlertRegistrationsResponseMsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link SetAlertRegistrationsResponseMsg }
     *     
     */
    public void setSetAlertRegistrationsResponseMsg(SetAlertRegistrationsResponseMsg value) {
        this.setAlertRegistrationsResponseMsg = value;
    }

    public boolean isSetSetAlertRegistrationsResponseMsg() {
        return (this.setAlertRegistrationsResponseMsg!= null);
    }

}
