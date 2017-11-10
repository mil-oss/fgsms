
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
 *         &lt;element name="SetAdministratorResult" type="{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}SetAdministratorResponseMsg"/>
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
    "setAdministratorResult"
})
@XmlRootElement(name = "SetAdministratorResponse")
public class SetAdministratorResponse
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "SetAdministratorResult", required = true, nillable = true)
    protected SetAdministratorResponseMsg setAdministratorResult;

    /**
     * Gets the value of the setAdministratorResult property.
     * 
     * @return
     *     possible object is
     *     {@link SetAdministratorResponseMsg }
     *     
     */
    public SetAdministratorResponseMsg getSetAdministratorResult() {
        return setAdministratorResult;
    }

    /**
     * Sets the value of the setAdministratorResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SetAdministratorResponseMsg }
     *     
     */
    public void setSetAdministratorResult(SetAdministratorResponseMsg value) {
        this.setAdministratorResult = value;
    }

    public boolean isSetSetAdministratorResult() {
        return (this.setAdministratorResult!= null);
    }

}
