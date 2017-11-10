
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
 *         &lt;element name="ElevateSecurityLevelResponseMsg" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}ElevateSecurityLevelResponseMsg"/>
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
    "elevateSecurityLevelResponseMsg"
})
@XmlRootElement(name = "ElevateSecurityLevelResponse")
public class ElevateSecurityLevelResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "ElevateSecurityLevelResponseMsg", required = true, nillable = true)
    protected ElevateSecurityLevelResponseMsg elevateSecurityLevelResponseMsg;

    /**
     * Gets the value of the elevateSecurityLevelResponseMsg property.
     * 
     * @return
     *     possible object is
     *     {@link ElevateSecurityLevelResponseMsg }
     *     
     */
    public ElevateSecurityLevelResponseMsg getElevateSecurityLevelResponseMsg() {
        return elevateSecurityLevelResponseMsg;
    }

    /**
     * Sets the value of the elevateSecurityLevelResponseMsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElevateSecurityLevelResponseMsg }
     *     
     */
    public void setElevateSecurityLevelResponseMsg(ElevateSecurityLevelResponseMsg value) {
        this.elevateSecurityLevelResponseMsg = value;
    }

    public boolean isSetElevateSecurityLevelResponseMsg() {
        return (this.elevateSecurityLevelResponseMsg!= null);
    }

}
