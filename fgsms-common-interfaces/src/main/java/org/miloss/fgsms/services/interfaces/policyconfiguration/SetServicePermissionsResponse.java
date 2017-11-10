
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
 *         &lt;element name="SetServicePermissionsResult" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}SetServicePermissionsResponseMsg"/>
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
    "setServicePermissionsResult"
})
@XmlRootElement(name = "SetServicePermissionsResponse")
public class SetServicePermissionsResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "SetServicePermissionsResult", required = true, nillable = true)
    protected SetServicePermissionsResponseMsg setServicePermissionsResult;

    /**
     * Gets the value of the setServicePermissionsResult property.
     * 
     * @return
     *     possible object is
     *     {@link SetServicePermissionsResponseMsg }
     *     
     */
    public SetServicePermissionsResponseMsg getSetServicePermissionsResult() {
        return setServicePermissionsResult;
    }

    /**
     * Sets the value of the setServicePermissionsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SetServicePermissionsResponseMsg }
     *     
     */
    public void setSetServicePermissionsResult(SetServicePermissionsResponseMsg value) {
        this.setServicePermissionsResult = value;
    }

    public boolean isSetSetServicePermissionsResult() {
        return (this.setServicePermissionsResult!= null);
    }

}
