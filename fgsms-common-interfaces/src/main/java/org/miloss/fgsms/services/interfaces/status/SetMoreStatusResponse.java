
package org.miloss.fgsms.services.interfaces.status;

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
 *         &lt;element name="SetStatusMoreResponseResult" type="{urn:org:miloss:fgsms:services:interfaces:status}SetStatusResponseMsg"/>
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
    "setStatusMoreResponseResult"
})
@XmlRootElement(name = "SetMoreStatusResponse")
public class SetMoreStatusResponse {

    @XmlElement(name = "SetStatusMoreResponseResult", required = true, nillable = true)
    protected SetStatusResponseMsg setStatusMoreResponseResult;

    /**
     * Gets the value of the setStatusMoreResponseResult property.
     * 
     * @return
     *     possible object is
     *     {@link SetStatusResponseMsg }
     *     
     */
    public SetStatusResponseMsg getSetStatusMoreResponseResult() {
        return setStatusMoreResponseResult;
    }

    /**
     * Sets the value of the setStatusMoreResponseResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SetStatusResponseMsg }
     *     
     */
    public void setSetStatusMoreResponseResult(SetStatusResponseMsg value) {
        this.setStatusMoreResponseResult = value;
    }

}
